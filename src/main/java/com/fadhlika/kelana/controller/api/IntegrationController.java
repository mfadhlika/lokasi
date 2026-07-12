package com.fadhlika.kelana.controller.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.kelana.dto.Response;
import com.fadhlika.kelana.dto.SaveIntegrationRequest;
import com.fadhlika.kelana.model.Integration;
import com.fadhlika.kelana.model.User;
import com.fadhlika.kelana.service.IntegrationService;
import com.fadhlika.kelana.service.OwntracksService;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationController {
    private final IntegrationService integrationService;

    private final OwntracksService owntracksService;

    IntegrationController(IntegrationService integrationService, OwntracksService owntracksService) {
        this.integrationService = integrationService;
        this.owntracksService = owntracksService;
    }

    @PutMapping
    public Response<Integration> saveIntegration(@RequestBody SaveIntegrationRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integration integration = new Integration(
                user.getId(),
                request.owntracksUsername(),
                request.owntracksPassword(),
                request.overlandApiKey());
        integrationService.saveIntegration(integration);

        Integration savedIntegration = integrationService.getIntegration(user.getId());

        return new Response<>(savedIntegration);
    }

    @GetMapping
    public Response<Integration> getIntegration() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Integration integration = integrationService.getIntegration(user.getId());

        return new Response<>(new Integration(integration.userId(), integration.owntracksUsername(), null,
                integration.overlandApiKey()));
    }

    @PostMapping("/owntracks/cmd/{action}")
    public Response<?> sendOwntracksCommand(@PathVariable String action) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        owntracksService.handleSendCommand(user, action);

        return new Response<>("success");
    }

}
