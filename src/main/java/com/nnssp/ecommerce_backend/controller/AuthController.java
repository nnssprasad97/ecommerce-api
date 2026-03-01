package com.nnssp.ecommerce_backend.controller;

import com.nnssp.ecommerce_backend.dto.AuthRequest;
import com.nnssp.ecommerce_backend.entity.User;
import com.nnssp.ecommerce_backend.exception.ResourceNotFoundException;
import com.nnssp.ecommerce_backend.exception.UnauthorizedException;
import com.nnssp.ecommerce_backend.repository.UserRepository;
import com.nnssp.ecommerce_backend.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public String register(@Valid @RequestBody AuthRequest request) {
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CUSTOMER) // Default role
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid password");
        }

        // Return the JWT Token
        return jwtUtil.generateToken(user.getEmail());
    }
}