 package com.mfano.mfes.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.BranchService;
import com.mfano.mfes.auth.services.ProfileService;
import com.mfano.mfes.auth.services.RoleService;
import com.mfano.mfes.auth.services.UserService;
import com.mfano.mfes.config.CustomUserDetails;

@Controller
public class BusinessController {
   private final UserService userService;
    // private final PostService postService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;

    public BusinessController(UserService userService, ProfileService profileService, AuditService auditService,
            RoleService roleService, BranchService storeService) {
        this.userService = userService;
        this.profileService = profileService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.storeService = storeService;
    }

    @GetMapping("/{bizz}")
    public String getBuss(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String bizz, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (bizz) {
            case "dashboard":

                dir = "business/index";
                break;
        }

        return dir;
    }
}
