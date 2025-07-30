package com.fadhlika.lokasi.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fadhlika.lokasi.dto.Response;
import com.fadhlika.lokasi.dto.SaveIntegrationRequest;
import com.fadhlika.lokasi.model.Integration;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.service.IntegrationService;

@RestController
@RequestMapping("/api/v1/integration")
public class IntegrationController {
    @Autowired
    private IntegrationService integrationService;

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

        return new Response<>(integration);
    }
}
