package com.example.ecommerce_app.ControllerUnitTest;

import com.example.ecommerce_app.Controllers.UserController;
import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.DTO.AuthResponse;
import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Services.AuthService;
import com.example.ecommerce_app.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController userController;


    private LocalUser user;
    private AuthResponse authResponse;


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
        authResponse = new AuthResponse("JWT_TOKEN", null, System.currentTimeMillis() + 60000);

    }

    @Test
    @DisplayName("1: Test user registration success")
    void registerUser_success() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);


        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    @DisplayName("2: Test registration with null email")
    void registerUser_nullEmail() {
        user.setEmail(null);
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
    }

    @Test
    @DisplayName("3: Test registration with empty email")
    void registerUser_emptyEmail() {
        user.setEmail("");
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody());
    }

    @Test
    @DisplayName("4: Test registration with null username")
    void registerUser_nullUsername() {
        user.setUsername(null);
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is required", response.getBody());
    }

    @Test
    @DisplayName("5: Test registration with empty username")
    void registerUser_emptyUsername() {
        user.setUsername("");
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is required", response.getBody());
    }

    @Test
    @DisplayName("6: Test registration with null password")
    void registerUser_nullPassword() {
        user.setPassword(null);
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
    }

    @Test
    @DisplayName("7: Test registration with empty password")
    void registerUser_emptyPassword() {
        user.setPassword("");
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password is required", response.getBody());
    }

    @Test
    @DisplayName("8: Test registration with existing email")
    void registerUser_emailExists() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    @DisplayName("9: Test registration with existing username")
    void registerUser_usernameExists() {
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userService.getUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        ResponseEntity<?> response = userController.register(user);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    @DisplayName("10: Test login with email success")
    void loginWithEmail_success() {
        when(userService.loginWithEmail(user.getEmail(), user.getPassword())).thenReturn(user);
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);
        ResponseEntity<?> response = userController.loginWithEmail(user.getEmail(), user.getPassword());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    @DisplayName("11: Test login with email fail")
    void loginWithEmail_fail() {
        when(userService.loginWithEmail(user.getEmail(), "wrong")).thenReturn(null);
        ResponseEntity<?> response = userController.loginWithEmail(user.getEmail(), "wrong");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    @DisplayName("12: Test login with username success")
    void loginWithUsername_success() {
        when(userService.loginWithUsername(user.getUsername(), user.getPassword())).thenReturn(user);
        when(authService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);
        ResponseEntity<?> response = userController.loginWithUsername(user.getUsername(), user.getPassword());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    @DisplayName("13: Test login with username fail")
    void loginWithUsername_fail() {
        when(userService.loginWithUsername(user.getUsername(), "wrong")).thenReturn(null);
        ResponseEntity<?> response = userController.loginWithUsername(user.getUsername(), "wrong");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    @DisplayName("14: Test getting all users")
    void getAllUsers_returnsList() {
        List<LocalUser> users = new ArrayList<>();
        users.add(user);
        when(userService.getAllUsers()).thenReturn(users);
        List<LocalUser> result = userController.getAllUsers();
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @Test
    @DisplayName("15: Test reset password")
    void resetPassword_success() {
        when(userService.resetPassword("test@example.com", "oldPass", "newPass"))
                .thenReturn("Password reset successfully");
        String result = userController.resetPassword("test@example.com", "oldPass", "newPass");
        assertEquals("Password reset successfully", result);
    }

    @Test
    @DisplayName("16: Test update profile")
    void updateProfile_success() {
        when(userService.updateUserDetails(eq(1L), any(LocalUser.class)))
                .thenReturn("User details updated successfully.");
        String result = userController.updateProfile(1L, user);
        assertEquals("User details updated successfully.", result);
    }

    @Test
    @DisplayName("17: Test get user details")
    void getUserDetails_success() {
        user.setUsername("testuser"); // Ensure it's explicitly set
        when(userService.getUserById(1L)).thenReturn(user);

        LocalUser result = userController.getUserDetails(1L);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername()); // safer than hardcoding
    }


    @Test
    @DisplayName("18: Test delete user")
    void deleteUser_success() {
        when(userService.deleteUser(1L)).thenReturn("User deleted successfully.");
        String result = userController.deleteUser(1L);
        assertEquals("User deleted successfully.", result);
    }
}