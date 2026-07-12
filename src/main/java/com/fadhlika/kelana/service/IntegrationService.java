package com.fadhlika.kelana.service;

import java.sql.SQLException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fadhlika.kelana.exception.InternalErrorException;
import com.fadhlika.kelana.model.Integration;
import com.fadhlika.kelana.repository.IntegrationRepository;
import com.fadhlika.kelana.util.RandomStringGenerator;

@Service
public class IntegrationService {

    private final IntegrationRepository integrationRepository;

    private final PasswordEncoder passwordEncoder;

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
