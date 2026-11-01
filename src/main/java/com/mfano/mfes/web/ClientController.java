package com.mfano.mfes.web;

import com.mfano.mfes.services.RepaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ClientController {
    private final RepaymentService repaymentService;
    public ClientController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @GetMapping("/clients/{id}/repayments")
    public String clientRepayments(@PathVariable Long id, Model model) {
        model.addAttribute("repayments", repaymentService.getAllRepaymentsForClient(id));
        model.addAttribute("dueRepayments", repaymentService.getDueRepaymentsForClient(id));
        model.addAttribute("totalPaid", repaymentService.getTotalPaidByClient(id));
        model.addAttribute("totalOutstanding", repaymentService.getTotalOutstandingByClient(id));
        return "client-repayments"; // new page
    }
}