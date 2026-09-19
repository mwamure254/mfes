 package com.mfano.mfes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bdo")
public class OfficerController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "business/index";
    }
}