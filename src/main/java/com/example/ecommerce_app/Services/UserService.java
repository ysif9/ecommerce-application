package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public String registerUser(LocalUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already registered.";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already taken.";
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            return "Password is required.";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
    public LocalUser loginWithEmail(String email, String password) {
        Optional<LocalUser> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            LocalUser user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;  // Return the actual user on successful login
            }
        }

        return null; // Login failed
    }


    public LocalUser loginWithUsername(String username, String password) {
        Optional<LocalUser> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            LocalUser user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;  // Return the actual user on successful login
            }
        }

        return null; // Login failed
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

        user.setPassword(passwordEncoder.encode(newPassword));
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
