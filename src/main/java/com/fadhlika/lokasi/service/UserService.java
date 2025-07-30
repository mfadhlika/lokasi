/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fadhlika.lokasi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fadhlika.lokasi.model.Integration;
import com.fadhlika.lokasi.model.User;
import com.fadhlika.lokasi.repository.UserRepository;

/**
 * @author fadhl
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IntegrationService integrationService;

    @Transactional
    public void createUser(String username, String password) {
        String hash = passwordEncoder.encode(password);
        User user = new User(username, hash);

        this.userRepository.createUser(user);

        user = userRepository.getUser(username);

        this.integrationService.saveIntegration(new Integration(
                user.getId()));
    }

    public void updateUser(int userId, String username, String password) {
        String hash = passwordEncoder.encode(password);
        User user = new User(userId, username, hash);
        this.userRepository.updateUser(user);
    }

    public boolean hasUsers() {
        return userRepository.hasUsers();
    }

    public List<String> getUserDevices(int userId) {
        return userRepository.getUserDevices(userId);
    }

    public User getUserByUserId(int userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return this.userRepository.getUser(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
