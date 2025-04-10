package com.example.ecommerce_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ecommerce_app.model.LocalUser;
import com.example.ecommerce_app.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String registerUser(LocalUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already registered.";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already taken.";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Password is required.";
        }

        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return "User registered successfully.";
    }

    public String deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            return "User not found.";
        }
        userRepository.deleteById(userId);
        return "User deleted successfully.";
    }

    // Get all users
    public List<LocalUser> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by email
    public Optional<LocalUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Get user by username
    public Optional<LocalUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Basic login logic
    public boolean loginWithEmail(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }

    public boolean loginWithUsername(String username, String password) {
        return userRepository.existsByUsernameAndPassword(username, password);
    }

    public String resetPassword(String email, String oldPassword, String newPassword) {
        Optional<LocalUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "User not found with this email";
        }

        LocalUser user = optionalUser.get();

        if (!user.getPassword().equals(oldPassword)) {
            return "Old password is incorrect";
        }

        if (newPassword == null || newPassword.length() < 6) {
            return "New password must be at least 6 characters";
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        return "Password reset successfully";
    }

    public LocalUser getUserById(Long id) {
        Optional<LocalUser> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public String updateUserDetails(Long id, LocalUser updatedInfo) {
        Optional<LocalUser> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "User not found.";
        }

        LocalUser user = optionalUser.get();
        user.setFirstName(updatedInfo.getFirstName());
        user.setLastName(updatedInfo.getLastName());
        user.setAddress(updatedInfo.getAddress());
        user.setPhoneNumber(updatedInfo.getPhoneNumber());

        userRepository.save(user);
        return "User details updated successfully.";
    }
}
