package com.fadhlika.lokasi.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fadhlika.lokasi.model.Integration;
import com.fadhlika.lokasi.repository.IntegrationRepository;

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
        String owntracksPasswordHash = this.passwordEncoder.encode(integration.owntracksPassword());
        integration = new Integration(
                integration.userId(),
                integration.owntracksEnable(),
                integration.owntracksUsername(),
                owntracksPasswordHash
        );
        try {
            integrationRepository.save(integration);
        } catch (SQLException ex) {
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

    public boolean validatePassword(String password, String hashPassword) {
        return passwordEncoder.matches(password, hashPassword);
    }
}
