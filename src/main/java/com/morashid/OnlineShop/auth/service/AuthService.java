package com.morashid.OnlineShop.auth.service;

import com.morashid.OnlineShop.auth.dto.AuthResponse;
import com.morashid.OnlineShop.auth.dto.LoginRequest;
import com.morashid.OnlineShop.auth.dto.RegisterRequest;
import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.auth.security.JwtService;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.user.entity.Role;
import com.morashid.OnlineShop.user.entity.User;
import com.morashid.OnlineShop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;  // ← ONGEZA HII

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNo(request.getPhoneNo())
                .role(Role.BUYER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Tengeneza JWT token
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phoneNo(savedUser.getPhoneNo())
                .role(savedUser.getRole())
                .message("Registration successful")
                .token(token)  // ← ONGEZA HII
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (!user.getEnabled()) {
            throw new BadRequestException("Account is disabled. Contact admin.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Tengeneza JWT token
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role(user.getRole())
                .message("Login successful")
                .token(token)  // ← ONGEZA HII
                .build();
    }
}