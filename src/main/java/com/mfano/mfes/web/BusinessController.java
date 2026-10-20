package com.mfano.mfes.web;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.BranchService;
import com.mfano.mfes.auth.services.RoleService;
import com.mfano.mfes.auth.services.UserService;
import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.models.GroupCollection;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.MemberCollection;
import com.mfano.mfes.models.Repayments;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.GroupCollectionService;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.LoanService;
import com.mfano.mfes.services.RepaymentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/business")
public class BusinessController {
    private final UserService userService;
    private final AuditService auditService;
    private final RoleService roleService;
    private final LoanService loanService;
    private final ClientService clientService;
    private final BranchService branchService;
    private final GroupService groupService;
    private final GroupCollectionService collectionService;
    private final RepaymentService repaymentService;

    public BusinessController(UserService userService, AuditService auditService, RepaymentService repaymentService,
            RoleService roleService, LoanService loanService, ClientService clientService, BranchService branchService,
            GroupService groupService, GroupCollectionService collectionService) {
        this.userService = userService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.loanService = loanService;
        this.clientService = clientService;
        this.branchService = branchService;
        this.groupService = groupService;
        this.collectionService = collectionService;
        this.repaymentService = repaymentService;
    }

    @GetMapping("/{option}")
    public String getBuss(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
        userService.getPrices(auth, red);

        String dir = "redirect:/";
        switch (option) {
            case "dashboard":

                dir = "business/index";
                break;

            case "collections":
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("collection", new GroupCollection());
                dir = "business/collections";
                break;

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
            loanService.update(id, loan);
            redirectAttributes.addFlashAttribute(
                    "message", "Loan updated successfully.");
        }

        return "redirect:/business/loans";
    }

    @PostMapping("/loans/{option}/{id}")
    public String manageFolio(
            @PathVariable Long id, @PathVariable String option,
            RedirectAttributes redirectAttribute) {
        Loan existing = loanService.getById(id).orElse(null);

        if (existing == null) {
            redirectAttribute.addFlashAttribute(
                    "error", "Loan not found: " + id);
            return "redirect:/business/loans";
        }
        switch (option) {
            case "approve":
                // loanService.toggleActive(existing);
                loanService.approve(id, existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Loan application approved successfully.");
                break;
            case "delete":
                loanService.delete(id);
                redirectAttribute.addFlashAttribute(
                        "message", "Loan deleted successfully.");
                break;

            default:
                break;
        }

        return "redirect:/business/loans";
    }

    @GetMapping("/collections/groups/{groupId}")
    public String showGroup(
            @PathVariable Long groupId, @AuthenticationPrincipal CustomUserDetails auth,
            Model model, GroupCollection collection, RedirectAttributes red) {
        userService.getPrices(auth, red);
        var group = groupService.getById(groupId);

        GroupCollection request = new GroupCollection();
        request.setGroupId(groupId);
        request.setPostedBy(auth.getEmail());
        request.setPaymentReference(collection.getPaymentReference());
        request.setPaymentAmount(collection.getPaymentAmount());
        request.setPaymentMethod(collection.getPaymentMethod());

        clientService.getByGroup(group).forEach(member -> {
            MemberCollection line = new MemberCollection();
            line.setMemberId(member.getId());
            request.getMembers().add(line);
        });

        model.addAttribute("groups", groupService.getAll());
        model.addAttribute("selectedGroup", group);
        model.addAttribute("collection", request);

        return "business/group-collection";
    }

    @PostMapping("/collections/groups")
    public String postCollection(@Valid @ModelAttribute("collection") GroupCollection request,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "groups", groupService.getAll());
            if (request.getGroupId() != null) {
                model.addAttribute(
                        "selectedGroup",
                        groupService.getById(
                                request.getGroupId()));
            }
            return "collections/group-collection";
        }

        try {
            String postedBy = user != null
                    ? user.getEmail()
                    : null;

            if (postedBy == null) {
                throw new IllegalStateException(
                        "Authentication is required to post collections.");
            }

            collectionService.postCollection(request, postedBy);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Group collection posted successfully.");

            return "redirect:/collections/groups";

        } catch (IllegalArgumentException ex) {
            bindingResult.reject("collection.invalid", ex.getMessage());
        } catch (IllegalStateException ex) {
            bindingResult.reject("collection.failed", ex.getMessage());
        }

        model.addAttribute("groups", groupService.getAll());

        if (request.getGroupId() != null) {
            model.addAttribute("selectedGroup",
                    groupService.getById(request.getGroupId()));
        }

        return "redirect:/business/collections";
    }

    @GetMapping("/dues")
    public String dues(
            @RequestParam(defaultValue = "all") String filter,
            @RequestParam(defaultValue = "30") int days,
            Model model, RedirectAttributes red, @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, red);
        LocalDate today = LocalDate.now();

        LocalDate startDate;
        LocalDate endDate;

        switch (filter.toLowerCase()) {
            case "overdue" -> {
                startDate = null;
                endDate = today.minusDays(1);
            }
            case "today" -> {
                startDate = today;
                endDate = today;
            }
            case "forecast" -> {
                startDate = today.plusDays(1);
                endDate = today.plusDays(Math.max(1, Math.min(days, 365)));
            }
            default -> {
                startDate = null;
                endDate = null;
            }
        }

        List<Repayments> dues;

        if ("overdue".equalsIgnoreCase(filter)) {
            dues = repaymentService.findOverdue(today);
        } else if (startDate != null && endDate != null) {
            dues = repaymentService.findUnpaidBetween(startDate, endDate);
        } else {
            dues = repaymentService.findAllUnpaid();
        }

        model.addAttribute("dues", dues);
        model.addAttribute("filter", filter);
        model.addAttribute("days", days);
        model.addAttribute("today", today);

        return "business/overdues";
    }

}
