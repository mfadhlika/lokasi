package com.fadhlika.lokasi.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.exception.InternalErrorException;
import com.fadhlika.lokasi.model.Integration;
import com.fadhlika.lokasi.repository.IntegrationRepository;
import com.fadhlika.lokasi.util.RandomStringGenerator;

@Service
public class IntegrationService {

    private final IntegrationRepository integrationRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public IntegrationService(IntegrationRepository integrationRepository, PasswordEncoder passwordEncoder) {
        this.integrationRepository = integrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveIntegration(Integration integration) {
        String owntracksUsername = integration.owntracksUsername();
        if (owntracksUsername.isBlank()) {
            owntracksUsername = "owntracks";
        }

        String owntracksPasswordHash = null;
        if (!integration.owntracksPassword().isBlank()) {
            owntracksPasswordHash = this.passwordEncoder.encode(integration.owntracksPassword());
        } else {
            owntracksPasswordHash = this.passwordEncoder.encode("owntracks");
        }

        String overlandApiKey = integration.overlandApiKey();

        if (overlandApiKey.isBlank()) {
            overlandApiKey = RandomStringGenerator.generate(16);
        }

        integration = new Integration(
                integration.userId(),
                owntracksUsername,
                owntracksPasswordHash,
                overlandApiKey);

        try {
            integrationRepository.save(integration);
        } catch (SQLException ex) {
            throw new InternalErrorException(ex.getMessage());
        }
    }

    public Integration getIntegration(int userId) {
        try {
            return integrationRepository.get(userId);
        } catch (SQLException ex) {
            throw new InternalError(ex.getMessage());
        }
    }

    public Integration getIntegrationByOwntracksUsername(String username) {
        try {
            return integrationRepository.getByOwntracksUsername(username);
        } catch (SQLException ex) {
            throw new InternalError(ex.getMessage());
        }
    }

    public Integration getIntegrationByOverlandApiKey(String apiKey) {
        try {
            return integrationRepository.getByOverlandApiKey(apiKey);
        } catch (SQLException ex) {
            throw new InternalError(ex.getMessage());
        }
    }

    public boolean validatePassword(String password, String hashPassword) {
        return passwordEncoder.matches(password, hashPassword);
    }
}
