package com.example.ecommerce_app.API_Controller;

import com.example.ecommerce_app.model.*;
import com.example.ecommerce_app.repository.*;
import com.example.ecommerce_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register") // register a new user
    public String register(@RequestBody LocalUser user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return "Email is required";
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return "Username is required";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Password is required";
        }

        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return "Email already exists";
        }

        if (userService.getUserByUsername(user.getUsername()).isPresent()) {
            return "Username already exists";
        }

        userService.registerUser(user);
        return "User registered successfully";
    }

    @GetMapping("/allUsers") // get all the users
    public List<LocalUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/login/email") // check login function using email
    public String loginWithEmail(@RequestParam String email, @RequestParam String password) {
        if (userService.loginWithEmail(email, password)) {
            return "Login successful";
        } else {
            return "Invalid email or password";
        }
    }

    @PostMapping("/login/username") //login with username
    public String loginWithUsername(@RequestParam String username, @RequestParam String password) {
        if (userService.loginWithUsername(username, password)) {
            return "Login successful";
        } else {
            return "Invalid username or password";
        }
    }

    @PutMapping("/reset-password") // resets the password
    public String resetPassword(String email, String oldPassword, String newPassword) {
        return userService.resetPassword(email, oldPassword, newPassword);
    }

    @PutMapping("/update-profile/{id}") // update user details
    public String updateProfile(@PathVariable Long id, @RequestBody LocalUser updatedInfo) {
        return userService.updateUserDetails(id, updatedInfo);
    }

    @GetMapping("/display-UserDetails/{id}") //display user details
    public LocalUser getUserDetails(@PathVariable Long id) {
        LocalUser user = userService.getUserById(id);
        if (user == null) {
            return null;
        }else {
            return user;
        }
    }

    @DeleteMapping("/delete/{id}") //delete user
    public String deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
