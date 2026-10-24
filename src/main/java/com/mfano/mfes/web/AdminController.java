package com.mfano.mfes.web;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.dtos.UserDto;
import com.mfano.mfes.auth.services.BranchService;
import com.mfano.mfes.auth.services.ProfileService;
import com.mfano.mfes.auth.models.Branch;
import com.mfano.mfes.auth.models.Profile;
import com.mfano.mfes.auth.models.Role;
import com.mfano.mfes.auth.models.User;
import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.RoleService;
import com.mfano.mfes.auth.services.UserService;

import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    // private final PostService postService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;

    public AdminController(UserService userService, ProfileService profileService, AuditService auditService,
            RoleService roleService, BranchService storeService) {
        this.userService = userService;
        this.profileService = profileService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.storeService = storeService;
    }

    // manage /GET/* module
    @GetMapping("/{option}")
    public String manage(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        if (auth.getRoles().contains("ADMIN")) {

        } else if (auth.getRoles().contains("CEO")) {

        } else if (auth.getRoles().contains("MANAGER")) {

        } else if (auth.getRoles().contains("PO")) {

        } else if (auth.getRoles().contains("BDO")) {

        } else {

        }
        switch (option) {
            case "dashboard":
                red.addAttribute("users", userService.findAll());
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("audits", auditService.findAll());
                dir = "admin/index";
                break;

            case "users":
                red.addAttribute("userDto", new UserDto());
                Long storeId = auth.getBranch().getId();

                if (storeId != null) {
                    // red.addAttribute("users", userService.findByBranch_Id(storeId));
                    red.addAttribute("users", userService.findAll());

                } else {
                    red.addAttribute("users", userService.findAll());
                }
                red.addAttribute("stores", storeService.findAll());
                red.addAttribute("roles", roleService.findAll());
                dir = "admin/users";
                break;

            case "branches":
                red.addAttribute("branches", storeService.findAll());
                red.addAttribute("branch", new Branch());
                dir = "admin/branches";
                break;

            case "roles":
                red.addAttribute("roles", roleService.findAll());
                red.addAttribute("stores", storeService.findAll());
                dir = "admin/roles";
                break;

            case "audits":
                red.addAttribute("auditEntries", auditService.findAll());
                dir = "admin/audits";
                break;
        }

        return dir;
    }

    @PostMapping("/branches/save")
    public String save(
            @ModelAttribute Branch branch,
            RedirectAttributes redirectAttributes) {

        if (storeService.findByCode(branch.getCode()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch code already exists.");

        }

        if (storeService.findByName(branch.getName()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch name already exists.");

        } else {
            storeService.createBranch(branch);

            redirectAttributes.addFlashAttribute(
                    "message", "Branch created successfully.");
        }
        return "redirect:/admin/branches";
    }

    @PostMapping("/branches/update/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute Branch updatedBranch,
            RedirectAttributes redirectAttributes) {

        Branch branch = storeService.findById(id);
        if (branch == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch not found: " + id);
        } else {
            branch.setName(updatedBranch.getName());
            branch.setCode(updatedBranch.getCode());
            branch.setLocation(updatedBranch.getLocation());
            branch.setPhone(updatedBranch.getPhone());
            branch.setEmail(updatedBranch.getEmail());

            storeService.createBranch(branch);

            redirectAttributes.addFlashAttribute(
                    "message", "Branch updated successfully.");
        }
        return "redirect:/admin/branches";
    }

    @PostMapping("/branches/toggle/{id}")
    public String toggle(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Branch branch = storeService.findById(id);
        if (branch == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Branch not found: " + id);
        } else {

            branch.setActive(!branch.isActive());
            storeService.createBranch(branch);

            redirectAttributes.addFlashAttribute(
                    "message", "Branch status updated successfully.");
        }
        return "redirect:/admin/branches";
    }

    @PostMapping("/branches/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        storeService.deleteBranch(id);

        redirectAttributes.addFlashAttribute(
                "message", "Branch deleted successfully.");

        return "redirect:/admin/branches";
    }

    // Role saving
    @PostMapping("/roles/save")
    public String saveRole(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute Role role,
            RedirectAttributes red) {
        try {
            roleService.save(role);
            auditService.record("CREATE_ROLE", "SUCCESS", auth.getEmail() + " Created role: " + role.getName());
            red.addFlashAttribute("message", "Role created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    // Role update
    @PostMapping("/roles/update/{id}")
    public String updateRole(
            @PathVariable Long id,
            @Valid Role role,
            RedirectAttributes redirectAttributes) {

        roleService.update(id, role);

        redirectAttributes.addFlashAttribute(
                "message",
                "Role updated successfully.");

        return "redirect:/admin/roles";
    }

    // manage /roles/* module
    @PostMapping("/roles/{option}/{id}")
    public String manage(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        String dir = "redirect";
        switch (option) {
            case "delete":
                roleService.deleteById(id);
                auditService.record("delete_role", "SUCCESS", auth.getEmail() + " Deleted role id=" + id);
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Role deleted successfully.");
                dir = "redirect:/admin/roles";
                break;

            case "toggle":
                roleService.toggleActive(id);
                auditService.record("toggle_role", "SUCCESS", auth.getEmail() + " toggled role id=" + id);
                redirectAttributes.addFlashAttribute(
                        "message",
                        "Role toggled successfully.");
                dir = "redirect:/admin/roles";
                break;
        }

        return dir;
    }

    // save user
    @PostMapping("/users/save")
    public String saveUser(@AuthenticationPrincipal CustomUserDetails auth, @ModelAttribute UserDto userDto,
            RedirectAttributes red) {
        try {
            userService.registerUser(userDto);
            auditService.record("create_user", "success", auth.getEmail() + " Created user: " + userDto.getEmail());
            red.addFlashAttribute("message", "User created successfully!");
        } catch (Exception e) {
            red.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // update user
    @PostMapping("/users/update/{id}")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails auth,
            @PathVariable Long id,
            @Valid UserDto userDto,
            RedirectAttributes redirectAttributes) {

        userService.update(id, userDto);
        auditService.record("update_user", "SUCCESS", auth.getEmail() + " Updated user: " + userDto.getEmail());
        redirectAttributes.addFlashAttribute(
                "message",
                "User updated successfully.");

        return "redirect:/admin/users";
    }

    @GetMapping("/manage/{id}")
    public String manageProfile(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id, Model model) {
        User user = userService.findById(id);
        userService.getPrices(auth, model);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", roleService.getUserRoles(user));
        model.addAttribute("userNotRoles", roleService.getUserNotRoles(user));
        return "admin/user-roles";
    }

    // manage /user route
    @PostMapping("/users/{option}/{id}")
    public String manageGetter(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option,
            @PathVariable Long id,
            Model model, RedirectAttributes red) {

        User user = userService.findById(id);

        String dir = "redirect";
        switch (option) {

            case "profile":
                // Add user info to model (for Thymeleaf dashboard pages)
                userService.getPrices(auth, model);
                model.addAttribute("user", user);
                model.addAttribute("profile1", profileService.checkProfile(user.getId()));
                dir = "admin/user-profile";
                break;

            // Resend verification link
            case "resend":
                if (user != null && !user.isEnabled()) {
                    userService.createAndSendToken(user);
                    auditService.record("RESEND_VERIFICATION", "SUCCESS",
                            auth.getEmail() + " Resent token to user id=" + id);
                    red.addFlashAttribute("message", "Link resent to user");
                    dir = "redirect:/admin/users";
                } else {
                    red.addFlashAttribute("error", "Sorry! Failed to send link.");
                    dir = "redirect:/admin/users";
                }
                break;

            // Delete user
            case "delete":
                try {
                    userService.deleteById(id);
                    auditService.record("delete_user", "success", auth.getEmail() + " Deleted user id=" + id);
                    red.addFlashAttribute("message", "user successfully deleted");
                    dir = "redirect:/admin/users";
                } catch (Exception e) {
                    red.addFlashAttribute("error", "Sorry! Failed to delete user");
                    dir = "redirect:/admin/users";
                }
                break;

            // Toggle user
            case "toggle":
                if (user != null) {
                    userService.toggleActive(id);
                    auditService.record("toggle_user",
                            "success",
                            auth.getEmail() + " Toggled user: " + user.getEmail() + " to enabled=" + user.isEnabled());
                    red.addFlashAttribute("message", "Toggled user successfully");
                    dir = "redirect:/admin/users";
                } else {
                    red.addFlashAttribute("error", "Sorry! Failed to toggle user");
                    dir = "redirect:/admin/users";
                }
                break;
        }

        return dir;
    }

    // profile/update @PreAuthorize("isAuthenticated()")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/profile/update/{id}")
    public String userProfileUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long id,
            @ModelAttribute("profile") Profile profile, RedirectAttributes red) {
        profileService.update(id, profile);
        auditService.record("update_profile", "success", auth.getEmail() + " Updated the profile of user id = " + id);
        red.addFlashAttribute("message", "Profile updated successfully");
        return "redirect:/admin/users";
    }

    // Update profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/update/{user}")
    public String imageUpdate(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long user,
            @RequestParam("image") MultipartFile file, RedirectAttributes red) {
        try {
            profileService.updateProfileImage(user, file, red);
            auditService.record("update_image", "success",
                    auth.getEmail() + " Updated the profile image  of user id = " + user);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // Delete profile image
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/image/delete/{user}")
    public String imageDelete(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long user,
            RedirectAttributes red) {

        try {
            profileService.deleteProfileImage(user, red);
            auditService.record("delete_image", "success",
                    auth.getEmail() + " Deleted the profile image of user id = " + user);
        } catch (IOException e) {
            red.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // Assign Remove roles
    @PostMapping("/{option}/{userId}/{roleId}")
    public String userRole(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable Long userId,
            @PathVariable String option,
            @PathVariable Long roleId, RedirectAttributes red) {

        switch (option) {
            case "assign-role":
                userService.assignRoleToUser(userId, roleId);
                auditService.record("update_role",
                        "success", auth.getEmail() + " Assigned user id=" + userId + " role id=" + roleId);
                red.addFlashAttribute("message", "Action successful");
                break;

            case "remove-role":
                userService.removeRoleFromUser(userId, roleId);
                auditService.record("update_role",
                        "SUCCESS", auth.getEmail() + " Revoked role id=" + roleId + " from user id=" + userId);
                red.addFlashAttribute("message", "Action successful");
                break;

            default:
                red.addFlashAttribute("error", "Action failed");
                break;
        }

        return "redirect:/admin/manage/{userId}";
    }
}