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
@RequestMapping("/executive")
public class ExecutiveController {
    private final UserService userService;
    // private final PostService postService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final BranchService storeService;

    public ExecutiveController(UserService userService, ProfileService profileService, AuditService auditService,
            RoleService roleService, BranchService storeService) {
        this.userService = userService;
        this.profileService = profileService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.storeService = storeService;
    }

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

                dir = "executive/index";
                break;
        }

        return dir;
    }
}
