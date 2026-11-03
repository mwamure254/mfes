package com.mfano.mfes.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mfano.mfes.models.Client;
import com.mfano.mfes.models.Referee;
import com.mfano.mfes.services.ClientService;
import com.mfano.mfes.services.RefereeService;

@Controller
@RequestMapping("/referees")
public class RefereeController {
    private final RefereeService refereeService;
    private final ClientService clientService;

    public RefereeController(RefereeService refereeService, ClientService clientService) {
        this.refereeService = refereeService;
        this.clientService = clientService;
    }

    @PostMapping("/clients/{id}")
    public String addReferee(@PathVariable Long id, @ModelAttribute Referee referee) {

        refereeService.saveReferee(id, referee);
        return "redirect:/manager/clients/" + id + "#referees";
    }

}
