package com.taskmanager.backend.controller;

import com.taskmanager.backend.repository.UserRepository;
import com.taskmanager.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    // Manual constructor for safety given the Java 24 environment
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // NOTE: Records use field names as methods (email() instead of getEmail())
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Authentication failed: User not found"));

        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    /**
     * Using Records avoids the "Cannot find symbol" errors caused by Lombok/Java 24 conflicts.
     * Records automatically provide constructors, getters, equals, hashCode, and toString.
     */
    public record AuthRequest(String email, String password) {}

    public record AuthResponse(String token) {}
}