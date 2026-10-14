package com.mfano.mfes.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.BranchService;
import com.mfano.mfes.auth.services.ProfileService;
import com.mfano.mfes.auth.services.RoleService;
import com.mfano.mfes.auth.services.UserService;
import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.Portfolio;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.PortfolioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    // private final PostService postService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final PortfolioService storeService;
    private final PortfolioService portfolioService;
    private final GroupService groupService;
    private final BranchService branchService;

    public ManagerController(UserService userService, ProfileService profileService, AuditService auditService,
            RoleService roleService, PortfolioService storeService, PortfolioService portfolioService,
        GroupService groupService, BranchService branchService) {
        this.userService = userService;
        this.profileService = profileService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.storeService = storeService;
        this.portfolioService = portfolioService;
        this.groupService = groupService;
        this.branchService = branchService;
    }

    @GetMapping("/{option}")
    public String manage(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "dashboard":
                dir = "manager/index";
                break;
            case "organization":
                red.addAttribute("portfolios", portfolioService.getAll());
                red.addAttribute("users", userService.findAll());
                dir = "manager/org";
                break;
            case "groups":
                red.addAttribute("groups", groupService.getAll());
                //red.addAttribute("clients", clientService.findAll());
                red.addAttribute("portfolios", portfolioService.getAll());
                red.addAttribute("branches", branchService.findAll());
                dir = "manager/groups";
                break;
        }

        return dir;
    }

    @PostMapping("/portfolios/save")
    public String saveFolio(
            @Valid Portfolio portfolio,
            RedirectAttributes redirectAttributes) {
        if (portfolioService.getByName(portfolio.getName()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Portfolio name already exists.");

        } else {
            portfolioService.save(portfolio);

            redirectAttributes.addFlashAttribute(
                    "message", "Portfolio created successfully.");
        }
        return "redirect:/manager/organization";
    }

    @PostMapping("/groups/save")
    public String saveGroup(
            @Valid Group group,
            RedirectAttributes redirectAttributes) {
        if (groupService.getByName(group.getName()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Group name already exists.");

        } else {
            groupService.save(group);

            redirectAttributes.addFlashAttribute(
                    "message", "Group created successfully.");
        }
        return "redirect:/manager/groups";
    }

    @PostMapping("/portfolios/update/{id}")
    public String updateFolio(
            @PathVariable Long id, @Valid Portfolio portfolio,
            RedirectAttributes redirectAttributes) {
        if (portfolioService.getById(id) == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Portfolio not found: " + id);
        } else {
            portfolioService.save(portfolio);
            redirectAttributes.addFlashAttribute(
                    "message", "Portfolio updated successfully.");
        }

        return "redirect:/manager/organization";
    }

    @PostMapping("/groups/update/{id}")
    public String updateGroup(
            @PathVariable Long id, @Valid Group group,
            RedirectAttributes redirectAttributes) {
        if (groupService.getById(id) == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Group not found: " + id);
        } else {
            groupService.save(group);
            redirectAttributes.addFlashAttribute(
                    "message", "Group updated successfully.");
        }

        return "redirect:/manager/groups";
    }

    @PostMapping("/portfolios/{option}/{id}")
    public String manageFolio(
            @PathVariable Long id, @PathVariable String option,
            RedirectAttributes redirectAttribute) {
        Portfolio existing;
        switch (option) {
            case "toggle":
                existing = portfolioService.getById(id);
                if (existing == null) {
                    redirectAttribute.addFlashAttribute(
                            "error", "Portfolio not found: " + id);
                }
                portfolioService.toggleActive(existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Portfolio status updated successfully.");
                break;
            case "delete":
                portfolioService.delete(id);
                redirectAttribute.addFlashAttribute(
                        "message", "Portfolio deleted successfully.");
                break;

            default:
                break;
        }

        return "redirect:/manager/organization";
    }

    @PostMapping("/groups/{option}/{id}")
    public String manageGroup(
            @PathVariable Long id, @PathVariable String option,
            RedirectAttributes redirectAttribute) {
        Group existing;
        switch (option) {
            case "toggle":
                existing = groupService.getById(id);
                if (existing == null) {
                    redirectAttribute.addFlashAttribute(
                            "error", "Group not found: " + id);
                }
                groupService.toggleActive(existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Group status updated successfully.");
                break;
            case "delete":
                groupService.delete(id);
                redirectAttribute.addFlashAttribute(
                        "message", "Group deleted successfully.");
                break;

            default:
                break;
        }

        return "redirect:/manager/groups";
    }
}
