package com.mfano.mfes.web;

import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.LoanService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/business")
public class BusinessController {
    private final UserService userService;
    private final ProfileService profileService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final LoanService loanService;
    private final ClientService clientService;
    private final BranchService branchService;
    private final GroupService groupService;

    public BusinessController(UserService userService, ProfileService profileService, AuditService auditService,
            RoleService roleService, LoanService loanService, ClientService clientService, BranchService branchService, GroupService groupService) {
        this.userService = userService;
        this.profileService = profileService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.loanService = loanService;
        this.clientService = clientService;
        this.branchService = branchService;
        this.groupService = groupService;
    }

    @GetMapping("/{option}")
    public String getBuss(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "loans":
                red.addAttribute("clients", clientService.getAll());
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("branches", branchService.findAll());
                red.addAttribute("loanApplications", loanService.getAll());
                dir = "business/loans";
                break;
        }

        return dir;
    }

    @PostMapping("/loans/save")
    public String saveFolio(
            @Valid Loan loan,
            RedirectAttributes redirectAttributes) {
        loanService.save(loan);

        redirectAttributes.addFlashAttribute(
                "message", "Loan created successfully.");
        return "redirect:/business/loans";
    }

    @PostMapping("/loans/update/{id}")
    public String updateFolio(
            @PathVariable Long id, @Valid Loan loan,
            RedirectAttributes redirectAttributes) {
        if (loanService.getById(id) == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Loan not found: " + id);
        } else {
            loanService.save(loan);
            redirectAttributes.addFlashAttribute(
                    "message", "Loan updated successfully.");
        }

        return "redirect:/business/loans";
    }

    @PostMapping("/portfolios/{option}/{id}")
    public String manageFolio(
            @PathVariable Long id, @PathVariable String option,
            RedirectAttributes redirectAttribute) {
        Optional<Loan> existing = loanService.getById(id);
        switch (option) {
            case "toggle":
                
                if (existing == null) {
                    redirectAttribute.addFlashAttribute(
                            "error", "Loan not found: " + id);
                }
                //loanService.toggleActive(existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Loan status updated successfully.");
                break;
            case "delete":
                loanService.delete(id);
                redirectAttribute.addFlashAttribute(
                        "message", "Loan deleted successfully.");
                break;

            default:
                break;
        }

        return "redirect:/manager/organization";
    }
}
