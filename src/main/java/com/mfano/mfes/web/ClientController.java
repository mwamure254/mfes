package com.mfano.mfes.web;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.RepaymentService;
import com.mfano.mfes.utils.documents.FileUpload;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final RepaymentService repaymentService;
    private final FileUpload fileUpload;
    private final ClientService clientService;

    public ClientController(FileUpload fileUpload, RepaymentService repaymentService,
            ClientService clientService) {
        this.repaymentService = repaymentService;
        this.fileUpload = fileUpload;
        this.clientService = clientService;
    }

    @GetMapping("/{id}/repayments")
    public String clientRepayments(@PathVariable Long id, Model model) {
        model.addAttribute("repayments", repaymentService.getAllRepaymentsForClient(id));
        model.addAttribute("dueRepayments", repaymentService.getDueRepaymentsForClient(id));
        model.addAttribute("totalPaid", repaymentService.getTotalPaidByClient(id));
        model.addAttribute("totalOutstanding", repaymentService.getTotalOutstandingByClient(id));
        return "client-repayments"; // new page
    }

    @PostMapping("/new")
    public String newClient(@ModelAttribute Client client,
            @RequestParam("passportPhoto") MultipartFile passport,
            @RequestParam("idFront") MultipartFile idFront,
            @RequestParam("idBack") MultipartFile idBack,
            @RequestParam("businessPhoto") MultipartFile business,
            @RequestParam("signature") MultipartFile signature) throws IOException {

        // 1. Save files to /uploads/clients/{idNumber}/
        String basePath = "documents/clients/" + client.getIdNumber() + "/";

        if (!passport.isEmpty())
            client.setPassportPath(fileUpload.uploadFile(passport, basePath));
        if (!idFront.isEmpty())
            client.setIdFrontPath(fileUpload.uploadFile(idFront, basePath));
        if (!idBack.isEmpty())
            client.setIdBackPath(fileUpload.uploadFile(idBack, basePath));
        if (!business.isEmpty())
            client.setBusinessPhotoPath(fileUpload.uploadFile(business, basePath));
        if (!signature.isEmpty())
            client.setSignaturePath(fileUpload.uploadFile(signature, basePath));

        clientService.save(client);
        return "redirect:/manager/clients";
    }
}