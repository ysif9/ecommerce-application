package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.DTO.AuthResponse;
import com.example.ecommerce_app.Model.AuthUser;
import com.example.ecommerce_app.Model.LocalUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private String jwtToken;
    private Long expiresAt;
    private LocalUser localUser;
    private AuthUser authUser;
    private AuthRequest authRequest;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Initialize common test objects
        String username = "testUser";
        String password = "testPassword";
        jwtToken = "jwt.token.value";
        expiresAt = 1234567890L;

        // Create a LocalUser with all required parameters
        localUser = new LocalUser();
        localUser.setID(1L);
        localUser.setUsername(username);
        localUser.setPassword(password);
        localUser.setEmail("test@mail.com");
        localUser.setFirstName("Test");
        localUser.setLastName("User");
        localUser.setAddress("Test Address");
        localUser.setCreatedAt(LocalDateTime.now());
        localUser.setRole("USER");
        localUser.setPhoneNumber("1234567890");

        authUser = new AuthUser(localUser);
        authRequest = new AuthRequest(username, password);

        // Create a mock Authentication object
        authentication = mock(Authentication.class);
    }

    @AfterEach
    void tearDown() {
        // Reset all mocks to ensure a clean state for each test
        reset(authenticationManager, jwtTokenService, userService);
    }


    @Test
    @DisplayName( "Test1: Authenticate a user successfully" )
    void testAuthenticate_SuccessfulAuthentication() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(authUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenService.generateToken(authentication)).thenReturn(jwtToken);
        when(jwtTokenService.extractExpirationTime(jwtToken)).thenReturn(expiresAt);

        // Act
        AuthResponse response = authService.authenticate(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(localUser.getID(), response.getUser().id());
        assertEquals(localUser.getUsername(), response.getUser().username());
        assertEquals(localUser.getEmail(), response.getUser().email());
        assertEquals(localUser.getFirstName(), response.getUser().firstName());
        assertEquals(localUser.getLastName(), response.getUser().lastName());
        assertEquals(localUser.getRole(), response.getUser().role());
        assertEquals(expiresAt, response.getExpiresAt());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenService, times(1)).generateToken(authentication);
        verify(jwtTokenService, times(1)).extractExpirationTime(jwtToken);
    }

    @Test
    @DisplayName( "Test2: Authenticate a user with invalid credentials" )
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenService);
    }

    @Test
    @DisplayName( "Test3: Authenticate a user with an invalid username" )
    void testAuthenticate_UserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> authService.authenticate(authRequest));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtTokenService);
    }

}
