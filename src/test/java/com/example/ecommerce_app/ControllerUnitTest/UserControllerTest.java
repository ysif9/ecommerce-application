package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.API_Controller.UserController;
import com.example.ecommerce_app.model.LocalUser;
import com.example.ecommerce_app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private LocalUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new LocalUser();
        user.setID(1);
        user.setEmail("saifsais@mail.com");
        user.setUsername("saifsais");
        user.setPassword("12345678");
        user.setFirstName("saif");
        user.setLastName("sais");
        user.setAddress("Cairo");
        user.setPhoneNumber("01234567899");
    }

    @Test
    @DisplayName("1: Test user registration success")
    void registerUser_success() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userService.registerUser(user)).thenReturn("User registered successfully");

        String result = userController.register(user);
        assertEquals("User registered successfully", result);
    }

    @Test
    @DisplayName("2: Test registration with null email")
    void registerUser_nullEmail() {
        user.setEmail(null);
        String result = userController.register(user);
        assertEquals("Email is required", result);
    }

    @Test
    @DisplayName("3: Test registration with empty email")
    void registerUser_emptyEmail() {
        user.setEmail("");
        String result = userController.register(user);
        assertEquals("Email is required", result);
    }

    @Test
    @DisplayName("4: Test registration with null username")
    void registerUser_nullUsername() {
        user.setUsername(null);
        String result = userController.register(user);
        assertEquals("Username is required", result);
    }

    @Test
    @DisplayName("5: Test registration with empty username")
    void registerUser_emptyUsername() {
        user.setUsername("");
        String result = userController.register(user);
        assertEquals("Username is required", result);
    }

    @Test
    @DisplayName("6: Test registration with null password")
    void registerUser_nullPassword() {
        user.setPassword(null);
        String result = userController.register(user);
        assertEquals("Password is required", result);
    }

    @Test
    @DisplayName("7: Test registration with empty password")
    void registerUser_emptyPassword() {
        user.setPassword("");
        String result = userController.register(user);
        assertEquals("Password is required", result);
    }

    @Test
    @DisplayName("8: Test registration with existing email")
    void registerUser_emailExists() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = userController.register(user);
        assertEquals("Email already exists", result);
    }

    @Test
    @DisplayName("9: Test registration with existing username")
    void registerUser_usernameExists() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        String result = userController.register(user);
        assertEquals("Username already exists", result);
    }

    @Test
    @DisplayName("10: Test login with email success")
    void loginWithEmail_success() {
        when(userService.loginWithEmail(user.getEmail(), user.getPassword())).thenReturn(user);
        String result = userController.loginWithEmail(user.getEmail(), user.getPassword());
        assertEquals("Login successful", result);
    }

    @Test
    @DisplayName("11: Test login with email fail")
    void loginWithEmail_fail() {
        when(userService.loginWithEmail(user.getEmail(), "wrong")).thenReturn(null);
        String result = userController.loginWithEmail(user.getEmail(), "wrong");
        assertEquals("Invalid email or password", result);
    }

    @Test
    @DisplayName("12: Test login with username success")
    void loginWithUsername_success() {
        when(userService.loginWithUsername(user.getUsername(), user.getPassword())).thenReturn(user);
        String result = userController.loginWithUsername(user.getUsername(), user.getPassword());
        assertEquals("Login successful", result);
    }

    @Test
    @DisplayName("13: Test login with username fail")
    void loginWithUsername_fail() {
        when(userService.loginWithUsername(user.getUsername(), "wrong")).thenReturn(null);
        String result = userController.loginWithUsername(user.getUsername(), "wrong");
        assertEquals("Invalid username or password", result);
    }

    @Test
    @DisplayName("14: Test reset password")
    void resetPassword_success() {
        when(userService.resetPassword(user.getEmail(), "oldPass", "newPass"))
                .thenReturn("Password reset successfully");
        String result = userController.resetPassword(user.getEmail(), "oldPass", "newPass");
        assertEquals("Password reset successfully", result);
    }

    @Test
    @DisplayName("15: Test update profile")
    void updateProfile_success() {
        when(userService.updateUserDetails(1L, user))
                .thenReturn("User details updated successfully.");
        String result = userController.updateProfile(1L, user);
        assertEquals("User details updated successfully.", result);
    }

    @Test
    @DisplayName("16: Test get user details found")
    void getUserDetails_found() {
        when(userService.getUserById(1L)).thenReturn(user);
        LocalUser result = userController.getUserDetails(1L);
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    @DisplayName("17: Test get user details not found")
    void getUserDetails_notFound() {
        when(userService.getUserById(2L)).thenReturn(null);
        LocalUser result = userController.getUserDetails(2L);
        assertNull(result);
    }

    @Test
    @DisplayName("18: Test delete user")
    void deleteUser() {
        when(userService.deleteUser(1L)).thenReturn("User deleted successfully.");
        String result = userController.deleteUser(1L);
        assertEquals("User deleted successfully.", result);
    }

    @Test
    @DisplayName("19: Test get all users")
    void getAllUsers() {
        List<LocalUser> users = new ArrayList<>();
        users.add(user);
        when(userService.getAllUsers()).thenReturn(users);
        List<LocalUser> result = userController.getAllUsers();
        assertEquals(1, result.size());
    }
}