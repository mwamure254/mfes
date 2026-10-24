package com.mfano.mfes.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.mfano.mfes.dtos.LoanRepaymentSummary;
import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.GroupCollection;
import com.mfano.mfes.models.Loan;
import com.mfano.mfes.models.MemberCollection;
import com.mfano.mfes.models.Repayments;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.GroupCollectionService;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.LoanService;
import com.mfano.mfes.services.PortfolioService;
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
    private final PortfolioService forlioService;

    public BusinessController(UserService userService, AuditService auditService, RepaymentService repaymentService,
            RoleService roleService, LoanService loanService, ClientService clientService, BranchService branchService,
            GroupService groupService, GroupCollectionService collectionService, PortfolioService forlioService) {
        this.userService = userService;
        this.auditService = auditService;
        this.roleService = roleService;
        this.loanService = loanService;
        this.clientService = clientService;
        this.branchService = branchService;
        this.groupService = groupService;
        this.collectionService = collectionService;
        this.repaymentService = repaymentService;
        this.forlioService = forlioService;
    }

    @GetMapping("/{option}")
    public String getBuss(@AuthenticationPrincipal CustomUserDetails auth, @PathVariable String option, Model red) {
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
                dir = "business/index";
                break;

            case "analytics":
                red.addAttribute("portfolios", forlioService.getAll());
                red.addAttribute("users", userService.findAll());
                dir = "business/analytics";
                break;

            case "collections":
                red.addAttribute("collection", collectionService.getAll());
                red.addAttribute("groups", groupService.getAll());

                for (GroupCollection col : collectionService.getAll()) {
                    red.addAttribute("officer", userService.findByEmail(col.getPostedBy()));
                    red.addAttribute("group", groupService.getById(col.getGroupId()));
                }

                dir = "business/collections";
                break;

            case "loans":
                red.addAttribute("clients", clientService.getAll());
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("branches", branchService.findAll());
                red.addAttribute("loanApplications", loanService.getAll());
                dir = "business/loans";
                break;

            case "approvals":
                red.addAttribute("clients", clientService.getAll());
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("branches", branchService.findAll());
                red.addAttribute("loanApplications", loanService.getAll());
                dir = "business/loans";
                break;

            case "disbursement":
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
            case "disburse":
                loanService.disburse(id, existing);
                redirectAttribute.addFlashAttribute(
                        "message", "Loan application disbursed successfully.");
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
            Model model) {
        userService.getPrices(auth, model);
        Group group = groupService.getById(groupId);

        GroupCollection collection = new GroupCollection();
        List<MemberCollection> allocations = clientService
                .getByGroup(group)
                .stream()
                .map(client -> {
                    MemberCollection line = new MemberCollection();
                    line.setMemberId(client.getId());
                    line.setSavingsAmount(BigDecimal.ZERO);
                    line.setLoanAmount(BigDecimal.ZERO);
                    return line;
                })
                .toList();

        collection.setMembers(new ArrayList<>(allocations));
        model.addAttribute("clients", clientService.getByGroup(group));
        model.addAttribute("officers", userService.findAll());
        model.addAttribute("group", group);
        model.addAttribute("collection", collection);

        return "business/group-collection";
    }

    @PostMapping("/collections/group")
    public String postCollection(@Valid @ModelAttribute("collection") GroupCollection request,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "groups", groupService.getAll());
            if (request.getGroupId() != null) {
                model.addAttribute(
                        "selectedGroup",
                        groupService.getById(
                                request.getGroupId()));
            }
        }

        try {

            if (request.getPostedBy() == null) {
                throw new IllegalStateException(
                        "Authentication is required to post collections.");
            }

            collectionService.postCollection(request, request.getPostedBy());

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Group collection posted successfully.");
            return "redirect:/business/collections";

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

        return "redirect: /business/collections/groups/" + request.getGroupId();
    }

    @GetMapping("/loans/repayments")
    public String repayments(
            @RequestParam(defaultValue = "overdue") String filter,
            @RequestParam(defaultValue = "1") int days,
            Model model, @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);
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
            case "all" -> {
                startDate = null;
                endDate = today.plusDays(Math.max(1, Math.min(days, 365)));
            }
            default -> {
                startDate = null;
                endDate = today;
            }
        }

        List<Repayments> dues;
        List<LoanRepaymentSummary> summary = null;
        Map<Long, Long> inst = new HashMap<>();

        if ("overdue".equalsIgnoreCase(filter)) {
            dues = repaymentService.findOverdue(today);
            summary = repaymentService.getLoanSummaries(today);
        } else if ("all".equalsIgnoreCase(filter)) {
            dues = repaymentService.findDuesUp(endDate);
            summary = repaymentService.getLoanSummaries(endDate);
        } else if (startDate != null && endDate != null) {
            dues = repaymentService.findUnpaidBetween(startDate, endDate);
            summary = repaymentService.getLoanSummaries(endDate);
        } else {
            dues = repaymentService.findDuesUp(today);
            summary = repaymentService.getLoanSummaries(today);
        }

        for (Repayments r : dues) {
            inst.put(
                    r.getLoan().getId(),
                    repaymentService.getCountByLoan(r.getLoan()));
        }

        model.addAttribute("dues", dues);
        model.addAttribute("summary", summary);
        model.addAttribute("inst", inst);
        model.addAttribute("filter", filter);
        model.addAttribute("days", days);
        model.addAttribute("today", today);

        return "business/repayments";
    }

    @GetMapping("/loans/dues")
    public String dues(
            @RequestParam(defaultValue = "overdue") String filter,
            @RequestParam(defaultValue = "1") int days,
            Model model, @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);
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
            case "all" -> {
                startDate = null;
                endDate = today.plusDays(Math.max(1, Math.min(days, 365)));
            }
            default -> {
                startDate = null;
                endDate = today;
            }
        }

        List<Loan> dues;
        Map<Long, Long> inst = new HashMap<>();
        Map<Long, LoanRepaymentSummary> summary = new HashMap<>();

        if ("overdue".equalsIgnoreCase(filter)) {
            dues = loanService.getByOverdue(today);
        } else if ("all".equalsIgnoreCase(filter)) {
            dues = loanService.getByOverdue(endDate);
        } else if (startDate != null && endDate != null) {
            dues = loanService.getByOverdueBetween(startDate, endDate);
        } else {
            dues = loanService.getByOverdue(today);
        }

        for (Loan l : dues) {
            inst.put(
                    l.getId(),
                    repaymentService.getCountByLoan(l));

            summary.put(
                    l.getId(),
                    repaymentService.getLoanRepaymentSummary(l, endDate));
        }

        model.addAttribute("dues", dues);
        model.addAttribute("summary", summary);
        model.addAttribute("inst", inst);
        model.addAttribute("filter", filter);
        model.addAttribute("days", days);
        model.addAttribute("today", today);

        return "business/overdues";
    }

    @GetMapping("/loans/arrears")
    public String arrears(
            Model model, @AuthenticationPrincipal CustomUserDetails auth) {
        userService.getPrices(auth, model);
        LocalDate today = LocalDate.now();

        List<Loan> dues = loanService.getByOverdue(today);
        Map<Long, Long> inst = new HashMap<>();
        Map<Long, LoanRepaymentSummary> summary = new HashMap<>();
            

        for (Loan l : dues) {
            inst.put(
                    l.getId(),
                    repaymentService.getCountByLoan(l));

            summary.put(
                    l.getId(),
                    repaymentService.getLoanRepaymentSummary(l, today));
        }

        model.addAttribute("dues", dues);
        model.addAttribute("summary", summary);
        model.addAttribute("inst", inst);

        return "business/arrears";
    }
}
