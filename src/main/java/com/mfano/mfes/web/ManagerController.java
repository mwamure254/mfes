package com.mfano.mfes.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.mfano.mfes.auth.services.AuditService;
import com.mfano.mfes.auth.services.BranchService;
import com.mfano.mfes.auth.services.UserService;
import com.mfano.mfes.config.CustomUserDetails;
import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Group;
import com.mfano.mfes.models.Portfolio;
import com.mfano.mfes.models.Referee;
import com.mfano.mfes.models.Repayments;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.GroupService;
import com.mfano.mfes.services.LoanService;
import com.mfano.mfes.services.PortfolioService;
import com.mfano.mfes.services.RepaymentService;
import com.mfano.mfes.utils.documents.FileUpload;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final FileUpload fileUpload_1;
    private final UserService userService;
    private final AuditService auditService;
    private final PortfolioService portfolioService;
    private final GroupService groupService;
    private final BranchService branchService;
    private final ClientService clientService;
    private final LoanService loanService;
    private final RepaymentService repaymentService;
    private final FileUpload fileUpload;

    public ManagerController(UserService userService, AuditService auditService,
            PortfolioService portfolioService, LoanService loanService,
            GroupService groupService, BranchService branchService, FileUpload fileUpload,
            ClientService clientService, RepaymentService repaymentService, FileUpload fileUpload_1) {
        this.userService = userService;
        this.auditService = auditService;
        this.portfolioService = portfolioService;
        this.groupService = groupService;
        this.branchService = branchService;
        this.clientService = clientService;
        this.repaymentService = repaymentService;
        this.loanService = loanService;
        this.fileUpload = fileUpload;
        this.fileUpload_1 = fileUpload_1;
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

            case "client-form":
                red.addAttribute("client", new Client());
                red.addAttribute("groups", groupService.getAll());
                red.addAttribute("branches", branchService.findAll());
                dir = "manager/client-registration";
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
    ////////////////////////////////////////
    /// To Do
    /// /////////////////////////////////

    @PostMapping("/clients/new")
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
        return "redirect:/clients";
    }

    @GetMapping("/clients/{id}")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("client", clientService.getById(id));
        model.addAttribute("loans", loanService.getByClient(clientService.getById(id)));
        // model.addAttribute("accounts", accountService.findByClient(id));
        // model.addAttribute("referees", refereeService.findByClient(id));
        // model.addAttribute("repayments", repaymentService.last10ByClient(id));
        return "client-profile";
    }

    @PostMapping("/clients/{id}/referees")
    public String addReferee(@PathVariable Long id, @ModelAttribute Referee referee) {
        // referee.setClientId(id);
        // refereeService.save(referee);
        return "redirect:/clients/" + id + "#referees";
    }

    @PostMapping("/repayments/record")
    public String recordRepayment(@ModelAttribute Repayments repayment) {
        // repaymentService.save(repayment);
        return "redirect:/clients/" + repayment.getLoan().getClient().getId() + "#repayments";
    }

    @PostMapping("/groups/new")
    public String newGroup(@ModelAttribute Group group,
            @RequestParam("certificate") MultipartFile cert,
            @RequestParam("constitution") MultipartFile constitution) throws IOException {

        String path = "documents/groups/" + group.getName().replace(" ", "_") + "/";

        // if(!cert.isEmpty()) group.setCertificatePath(saveFile(cert, path));
        // if(!constitution.isEmpty()) group.setConstitutionPath(saveFile(constitution,
        // path));

        groupService.save(group);
        return "redirect:/groups";
    }

    @GetMapping("/groups/{id}")
    public String groupProfile(@PathVariable Long id, Model model) {
        model.addAttribute("group", groupService.getById(id));
        model.addAttribute("clients", clientService.getByGroup(groupService.getById(id)));
        // model.addAttribute("groupLoans", loanService.getGroupLoans(id));
        return "group-profile";
    }

    /*
     * @PostMapping("/groups/{id}/bulk-upload")
     * public String bulkUpload(@PathVariable Long id, @RequestParam("excelFile")
     * MultipartFile file,
     * RedirectAttributes redirect) {
     * try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
     * Sheet sheet = workbook.getSheetAt(0);
     * int success = 0, failed = 0;
     * 
     * for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
     * Row row = sheet.getRow(i);
     * if (row == null)
     * continue;
     * 
     * try {
     * Client client = new Client();
     * client.setGroupId(id);
     * client.setFirstName(getCellValue(row.getCell(0)));
     * client.setLastName(getCellValue(row.getCell(1)));
     * client.setIdNumber(getCellValue(row.getCell(2)));
     * client.setPhone(getCellValue(row.getCell(3)));
     * client.setGender(getCellValue(row.getCell(4)));
     * client.setDob(parseDate(getCellValue(row.getCell(5))));
     * client.setBusinessName(getCellValue(row.getCell(6)));
     * client.setBusinessType(getCellValue(row.getCell(7)));
     * client.setMonthlyIncome(parseBigDecimal(getCellValue(row.getCell(8))));
     * client.setAddress(getCellValue(row.getCell(9)));
     * 
     * clientService.save(client);
     * success++;
     * } catch (Exception e) {
     * failed++;
     * log.error("Row " + (i + 1) + " failed: " + e.getMessage());
     * }
     * }
     * redirect.addFlashAttribute("message", success +
     * " members uploaded successfully. " + failed + " failed.");
     * 
     * } catch (IOException e) {
     * redirect.addFlashAttribute("error", "Failed to read Excel file");
     * }
     * return "redirect:/groups/" + id + "#members";
     * }
     * 
     * @GetMapping("/branches/{id}")
     * public String branchProfile(@PathVariable Long id, Model model){
     * Branch branch = branchService.findById(id);
     * model.addAttribute("branch", branch);
     * model.addAttribute("portfolios", portfolioService.findByBranch(id));
     * model.addAttribute("groups", groupService.findByBranch(id));
     * model.addAttribute("totalClients", clientService.countByBranch(id));
     * model.addAttribute("totalPortfolio", loanService.sumBalanceByBranch(id));
     * model.addAttribute("totalSavings", savingsService.sumByBranch(id));
     * model.addAttribute("par", reportService.calculateParByBranch(id));
     * model.addAttribute("parReport", reportService.parByPortfolio(id));
     * return "branch-profile";
     * }
     */
    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    /*
     * @GetMapping("/dashboard")
     * public String dashboard(@RequestParam(defaultValue = "MONTH") String range,
     * Model model){
     * 
     * LocalDate startDate, endDate = LocalDate.now();
     * String label;
     * 
     * switch(range){
     * case "TODAY":
     * startDate = LocalDate.now();
     * label = "Today - " +
     * LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
     * break;
     * case "YEAR":
     * startDate = LocalDate.now().withDayOfYear(1);
     * label = "Jan - Dec " + LocalDate.now().getYear();
     * break;
     * case "ALL":
     * startDate = LocalDate.of(2020,1,1); // system start date
     * label = "All Time";
     * break;
     * default: // MONTH
     * startDate = LocalDate.now().withDayOfMonth(1);
     * label = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
     * }
     * 
     * model.addAttribute("range", range);
     * model.addAttribute("dateRangeLabel", label);
     * 
     * // Pass dates to all services
     * model.addAttribute("totalClients",
     * clientService.countCreatedBetween(startDate, endDate));
     * model.addAttribute("newClientsCount",
     * clientService.countCreatedBetween(startDate, endDate));
     * model.addAttribute("disbursedAmount",
     * loanService.sumDisbursedBetween(startDate, endDate));
     * model.addAttribute("recoveryAmount", repaymentService.sumBetween(startDate,
     * endDate));
     * model.addAttribute("branchPerformance",
     * reportService.branchPerformance(startDate, endDate));
     * model.addAttribute("chartLabels", reportService.getChartLabels(range));
     * model.addAttribute("chartLoanData", reportService.getLoanChartData(range));
     * 
     * return "dashboard";
     * }
     */
}
