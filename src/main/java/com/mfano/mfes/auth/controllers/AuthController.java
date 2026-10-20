package com.mfano.mfes.auth.controllers;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.dtos.UserDto;
import com.mfano.mfes.auth.models.Profile;
import com.mfano.mfes.auth.models.User;
import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.ProfileService;
import com.mfano.mfes.auth.services.RoleService;
import com.mfano.mfes.auth.services.UserService;

@Controller
public class AuthController {
    private final UserService userService;
    private final RoleService roleService;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    private String msg = "security/message";
    private String login = "redirect:/login";

    private final AuditService auditService;

    public AuthController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder,
            AuditService auditService, ProfileService profileService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.profileService = profileService;
    }

    // guest user
    @GetMapping("/")
    public String redirectLogin(@AuthenticationPrincipal CustomUserDetails auth, RedirectAttributes model) {

        if (auth == null) {
            return "redirect:/login";
        }

        // Extract roles
        Set<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // If user is not assigned any role
        if (roles.isEmpty()) {
            model.addFlashAttribute("error", "Please contact the system admin for mapping.");
            return login;
        } else if (!auth.isEnabled()) {
            model.addFlashAttribute("error", "Contact the system admin for account verification.");
            return login;
        }
        // model.addFlashAttribute("profile",
        // profileService.checkProfile(auth.getId()));

        // Redirect based on role priority
        if (roles.contains("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (roles.contains("CEO")) {
            return "redirect:/executive/dashboard";
        } else if (roles.contains("ROLE_MANAGER")) {
            return "redirect:/manager/dashboard";
        } else if (roles.contains("ROLE_PO")) {
            return "redirect:/inventory/dashboard";
        } else if (roles.contains("ROLE_BDO")) {
            return "redirect:/business/dashboard";
        } else {
            model.addFlashAttribute("error", "Please contact the system admin for role mapping.");
            return login;
        }

    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("userDto", new UserDto());
        return "security/register";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            RedirectAttributes model,
            Authentication authentication) {

        // If user is already logged in → redirect to dashboard
        if (authentication != null && authentication.isAuthenticated()
                && authentication instanceof CustomUserDetails) {
            return "redirect:/dashboard";
        }

        // Logout confirmation
        if (logout != null) {
            model.addFlashAttribute("message", "You have been logged out.");
        }

        return "security/login"; // Return login view
    }

    @GetMapping("/error/403")
    public String forbidden() {
        return "error/403";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/contact")
    public String contact(Model model,
            @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);

        return "security/contact";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model,
            @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);

        return "security/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "") String query, Model model,
            @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);
        model.addAttribute("query", query);
        // Uncomment when your search service is ready
        // model.addAttribute("results", searchService.search(query));

        return "security/search";
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes model) {
        model.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login?logout";
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth,
            @ModelAttribute("profile") Profile profile) {

        profileService.update(auth.getId(), profile);
        auditService.record("update_profile", "SUCCESS", "user id=" + auth.getId() + "Updated their profile");
        return "redirect:/profile";
    }

    // update user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{userid}")
    public String imageUpdate(@PathVariable Long userid, @RequestParam("image") MultipartFile file,
            RedirectAttributes red) {
        try {
            profileService.updateProfileImage(userid, file, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record("update_image", "SUCCESS", "user id=" + userid + "Updated their profile image");
        red.addFlashAttribute("message", "Image updated successfully.");
        return "redirect:/profile";
    }

    // delete user image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{userid}")
    public String imageDelete(@PathVariable Long userid, RedirectAttributes red) {
        try {
            profileService.deleteProfileImage(userid, red);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }
        auditService.record("delete_image", "SUCCESS", "user id=" + userid + "Deleted their profile image");
        red.addFlashAttribute("message", "Image deleted successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token, Model model) {
        String result = userService.validateVerificationToken(token);
        if ("valid".equals(result)) {
            model.addAttribute("message", "Email verified! You can now login.");

        } else if ("expired".equals(result)) {
            model.addAttribute("error", "Token expired. Please register again.");

        } else {
            model.addAttribute("error", "Invalid token.");
        }
        return login;
    }

    @GetMapping("/resend")
    public String resendForm() {
        return "security/resend";
    }

    @PostMapping("/resend")
    public String resendSubmit(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account with that email.");
            return "security/resend";
        } else if (user.isEnabled()) {
            model.addAttribute("message", "Email already verified. You can login.");
            return login;
        } else {
            userService.createAndSendToken(user);
            model.addAttribute("message", "Verification email resent. Check your inbox.");
            return login;
        }
    }

    // Forgot/reset endpoints
    @GetMapping("/forgot")
    public String forgotForm() {
        return "security/forgot";
    }

    @PostMapping("/forgot")
    public String forgotSubmit(@RequestParam String email, RedirectAttributes model) {
        if (userService.findByEmail(email) == null) {
            model.addFlashAttribute("error", "No account matches the email address.");
            return "redirect:/forgot";
        }

        try {
            userService.createPasswordResetToken(email);
            model.addFlashAttribute("message", "Check your email, a reset link was sent.");
        } catch (Exception e) {
            model.addFlashAttribute("error", "Something went wrong, please try again.");
            return "redirect:/forgot";
        }
        return "redirect:/login";
    }

    // self-serve password email change
    @GetMapping("/password-reset")
    public String resetPasswordForm(@RequestParam("token") String token, Model model) {
        String res = userService.validatePasswordResetToken(token);
        if ("valid".equals(res)) {
            model.addAttribute("token", token);
            return "security/reset-password";
        } else if ("expired".equals(res)) {
            model.addAttribute("error", "Token expired.");
            return login;
        } else {
            model.addAttribute("error", "Invalid token.");
            return login;
        }
    }

    // self-serve password change request
    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token, @RequestParam String password, Model model) {
        var optUser = userService.getUserByPasswordResetToken(token);
        if (optUser.isEmpty()) {
            model.addAttribute("error", "Invalid token.");
            return login;
        }
        userService.changePassword(optUser.get(), password);
        model.addAttribute("message", "Password changed. You can now login.");
        return login;
    }

    // logged user change password
    // Reset user password
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/reset")
    public String resetPassword(@AuthenticationPrincipal CustomUserDetails auth, @RequestParam String password,
            @RequestParam String NP,
            RedirectAttributes red) {
        User user = userService.findById(auth.getId());
        if (!NP.equals(password) || NP.isEmpty() || password.isEmpty()) {
            red.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/profile";
        }

        if (user != null) {
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            auditService.record("reset_password", "SUCCESS", "user id=" + user.getId() + "Reset password");
            red.addFlashAttribute("message", "Password reset successful");
            return "redirect:/profile";
        } else {
            red.addFlashAttribute("error", "Failed to reset password");
            return "redirect:/profile";
        }
    }
}
