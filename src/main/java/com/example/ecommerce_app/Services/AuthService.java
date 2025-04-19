package com.example.ecommerce_app.Services;

import com.example.ecommerce_app.DTO.AuthRequest;
import com.example.ecommerce_app.DTO.AuthResponse;
import com.example.ecommerce_app.DTO.UserAuthResponse;
import com.example.ecommerce_app.Model.AuthUser;
import com.example.ecommerce_app.Model.LocalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public AuthResponse authenticate(AuthRequest authRequest) {
        var token = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);

        String jwtToken = jwtTokenService.generateToken(authentication);
        Long expiresAt = jwtTokenService.extractExpirationTime(jwtToken);

        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        LocalUser user = authUser.getUser();

        UserAuthResponse userAuthResponse = new UserAuthResponse(
                user.getID(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );

        return new AuthResponse(jwtToken, userAuthResponse, expiresAt);
    }

    public LocalUser getUserFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String username = jwt.getSubject();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
