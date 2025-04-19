package com.example.ecommerce_app.Controllers;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Services.AuthService;
import com.example.ecommerce_app.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/// /
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping("/register") // register a new user
    public ResponseEntity<?> register(@RequestBody LocalUser user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is required");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required");
        }

        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists");

        }

        if (userService.getUserByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        String rawPassword = user.getPassword();
        userService.registerUser(user);
        var response = authService.authenticate(new AuthRequest(user.getUsername(), rawPassword));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/allUsers") // get all the users
    public List<LocalUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/login/email") // check login function using email
    public ResponseEntity<?> loginWithEmail(@RequestParam String email, @RequestParam String password) {
        LocalUser user = userService.loginWithEmail(email, password);
        if (user != null) {
            var response = authService.authenticate(new AuthRequest(user.getUsername(), password));
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/login/username") //login with username
    public ResponseEntity<?> loginWithUsername(@RequestParam String username, @RequestParam String password) {
        if (userService.loginWithUsername(username, password) != null) {
            var response = authService.authenticate(new AuthRequest(username, password));
            return ResponseEntity.ok(response);
//            return "Login successful";
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
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
        return userService.getUserById(id);
    }

    @DeleteMapping("/delete/{id}") //delete user by id
    public String deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
