package com.mfano.mfes.web;

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
import com.mfano.mfes.auth.services.UserService;
import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.Portfolio;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.PortfolioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    private final AuditService auditService;
    private final PortfolioService portfolioService;
    private final GroupService groupService;
    private final BranchService branchService;
    private final ClientService clientService;

    public ManagerController(UserService userService, AuditService auditService,
            PortfolioService portfolioService,
            GroupService groupService, BranchService branchService, ClientService clientService) {
        this.userService = userService;
        this.auditService = auditService;
        this.portfolioService = portfolioService;
        this.groupService = groupService;
        this.branchService = branchService;
        this.clientService = clientService;
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
                dir = "manager/index";
                break;
            case "organization":
                red.addAttribute("portfolios", portfolioService.getAll());
                red.addAttribute("users", userService.findAll());
                dir = "manager/org";
                break;
            case "groups":
                red.addAttribute("groups", groupService.getAll());
                // red.addAttribute("clients", clientService.findAll());
                red.addAttribute("portfolios", portfolioService.getAll());
                red.addAttribute("branches", branchService.findAll());
                dir = "manager/groups";
                break;
            case "clients":
                red.addAttribute("clients", clientService.getAll());
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("branches", branchService.findAll());
                dir = "manager/clients";
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
        return "redirect:/business/portfolios";
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

    @PostMapping("/clients/save")
    public String saveClient(
            @Valid Client client,
            RedirectAttributes redirectAttributes) {
        if (clientService.getByPhone(client.getPhone()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Client phone already exists.");

        } else if (clientService.getByNumber(client.getIdNumber()) != null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Client ID number already exists.");
        } else {
            clientService.save(client);

            redirectAttributes.addFlashAttribute(
                    "message", "Client created successfully.");
        }
        return "redirect:/manager/clients";
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

        return "redirect:/business/portfolios";
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

    @PostMapping("/clients/update/{id}")
    public String updateGroup(
            @PathVariable Long id, @Valid Client client,
            RedirectAttributes redirectAttributes) {
        if (clientService.getById(id) == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Client not found: " + id);
        } else {
            clientService.save(client);
            redirectAttributes.addFlashAttribute(
                    "message", "Client updated successfully.");
        }

        return "redirect:/manager/clients";
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

        return "redirect:/business/portfolios";
    }

    @PostMapping("/clients/{option}/{id}")
    public String manageClient(
            @PathVariable Long id, @PathVariable String option,
            RedirectAttributes redirectAttribute) {
        Client existing;
        switch (option) {
            case "toggle":
                existing = clientService.getById(id);
                if (existing == null) {
                    redirectAttribute.addFlashAttribute(
                            "error", "Client not found: " + id);
                }
                clientService.toggleActive(existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Client status updated successfully.");
                break;
            case "delete":
                groupService.delete(id);
                redirectAttribute.addFlashAttribute(
                        "message", "Client deleted successfully.");
                break;

            default:
                break;
        }

        return "redirect:/manager/clients";
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
