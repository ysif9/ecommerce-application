package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.Model.LocalUser;
import com.example.ecommerce_app.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    private LocalUser user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new LocalUser();
        user.setID(1);
        user.setEmail("saifsais@mail.com");
        user.setUsername("saifsais");
        user.setPassword("123456");
        user.setFirstName("saif");
        user.setLastName("sais");
        user.setAddress("Cairo");
        user.setPhoneNumber("01234567899");
    }

    @Test
    @DisplayName("1: Test a successful case of registration")
    void registerUser_success() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        user.setPassword("validPass123");

        String result = userService.registerUser(user);

        assertEquals("User registered successfully.", result);
        verify(userRepository).save(any(LocalUser.class));
    }


    @Test
    @DisplayName("2: Test a failed case of registration by email")
    void registerUser_emailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        String result = userService.registerUser(user);
        assertEquals("Email already registered.", result);
    }

    @Test
    @DisplayName("3: Test a failed case of registration by username")
    void registerUser_usernameExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        String result = userService.registerUser(user);
        assertEquals("Username already taken.", result);
    }

    @Test
    @DisplayName("4: Test a failed case of setting password by invalid password")
    void registerUser_weakPassword() {
        user.setPassword("123");
        String result = userService.registerUser(user);
        assertEquals("Password is required.", result);
    }

    @Test
    @DisplayName("5: Test failed registration when password is null")
    void registerUser_nullPassword() {
        user.setPassword(null); // simulate null password
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);

        String result = userService.registerUser(user);
        assertEquals("Password is required.", result);
    }


    @Test
    @DisplayName("6: Test displaying all users")
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        assertEquals(1, userService.getAllUsers().size());
    }

    @Test
    @DisplayName("7: Test display user by email")
    void getUserByEmail_found() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertTrue(userService.getUserByEmail(user.getEmail()).isPresent());
    }

    @Test
    @DisplayName("8: Test display user by username")
    void getUserByUsername_found() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        assertTrue(userService.getUserByUsername(user.getUsername()).isPresent());
    }

    @Test
    @DisplayName("9: Test successful case of login using email")
    void loginWithEmail_success() {
        String rawPassword = "12345678";
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);

        LocalUser result = userService.loginWithEmail(user.getEmail(), rawPassword);

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }


    @Test
    @DisplayName("10: Test successful case of login using username")
    void loginWithUsername_success() {
        String rawPassword = "12345678";
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, "encodedPassword")).thenReturn(true);

        LocalUser result = userService.loginWithUsername(user.getUsername(), rawPassword);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }


    @Test
    @DisplayName("11: Test failure case of login using email")
    void loginWithEmail_fail() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        LocalUser result = userService.loginWithEmail(user.getEmail(), "wrong");
        assertNull(result);
    }

    @Test
    @DisplayName("12: Test failure case of login using username")
    void loginWithUsername_fail() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        LocalUser result = userService.loginWithUsername(user.getUsername(), "wrong");
        assertNull(result);
    }

    @Test
    @DisplayName("13: Test failure case of login using password in email")
    void loginWithEmail_wrongPassword() {
        user.setPassword("encodedPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);
        LocalUser result = userService.loginWithEmail(user.getEmail(), "wrongPass");
        assertNull(result);
    }

    @Test
    @DisplayName("14: Test failure case of login using password in username")
    void loginWithUsername_wrongPassword() {
        user.setPassword("encodedPassword");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);
        LocalUser result = userService.loginWithUsername(user.getUsername(), "wrongPass");
        assertNull(result);
    }

    @Test
    @DisplayName("15: Test ID getter successful")
    void getUserById_found() {
        when(userRepository.findById(user.getID())).thenReturn(Optional.of(user));
        assertNotNull(userService.getUserById(user.getID()));
    }

    @Test
    @DisplayName("16: Test ID getter failure")
    void getUserById_notFound() {
        when(userRepository.findById(user.getID())).thenReturn(Optional.empty());
        assertNull(userService.getUserById(user.getID()));
    }

    @Test
    @DisplayName("17: Update User details successful Using the userID")
    void updateUserDetails_success() {
        LocalUser updated = new LocalUser();
        updated.setFirstName("new");
        updated.setLastName("name");
        updated.setAddress("Alex");
        updated.setPhoneNumber("01111222333");
        when(userRepository.findById(user.getID())).thenReturn(Optional.of(user));
        String result = userService.updateUserDetails(user.getID(), updated);
        assertEquals("User details updated successfully.", result);
    }

    @Test
    @DisplayName("18: Update User details failed Using the userID")
    void updateUserDetails_userNotFound() {
        when(userRepository.findById(user.getID())).thenReturn(Optional.empty());
        String result = userService.updateUserDetails(user.getID(), user);
        assertEquals("User not found.", result);
    }

    @Test
    @DisplayName("19: Test reset password")
    void resetPassword_success() {
        user.setPassword("oldPass");
        when(userService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = userService.resetPassword(user.getEmail(), "oldPass", "newStrongPassword");
        assertEquals("Password reset successfully", result);
    }

    @Test
    @DisplayName("20: Reset password failed as user is not found")
    void resetPassword_userNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        String result = userService.resetPassword(user.getEmail(), "any", "newpass");
        assertEquals("User not found with this email", result);
    }

    @Test
    @DisplayName("21: Reset password failed as old password is wrong")
    void resetPassword_wrongOldPassword() {
        user.setPassword("correctPass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = userService.resetPassword(user.getEmail(), "wrongPass", "newpass");
        assertEquals("Old password is incorrect", result);
    }

    @Test
    @DisplayName("22: Reset password failed as new password is set as null")
    void resetPassword_nullNewPassword() {
        user.setPassword("oldPass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = userService.resetPassword(user.getEmail(), "oldPass", null);
        assertEquals("New password must be at least 6 characters", result);
    }

    @Test
    @DisplayName("23: Reset password failed as new password is set as weak")
    void resetPassword_shortNewPassword() {
        user.setPassword("oldPass");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        String result = userService.resetPassword(user.getEmail(), "oldPass", "123");
        assertEquals("New password must be at least 6 characters", result);
    }

    @Test
    @DisplayName("24: Delete user failed as user not found")
    void deleteUser_userNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        String result = userService.deleteUser(2L);
        assertEquals("User not found.", result);
    }

    @Test
    @DisplayName("25: Delete user successful")
    void deleteUser_userFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        String result = userService.deleteUser(1L);
        assertEquals("User deleted successfully.", result);
        verify(userRepository).deleteById(1L);
    }
}
