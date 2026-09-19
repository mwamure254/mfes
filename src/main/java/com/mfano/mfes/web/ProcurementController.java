 package com.mfano.mfes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/procurement")
public class ProcurementController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "procurement/index";
    }
}
