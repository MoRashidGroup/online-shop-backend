package com.morashid.OnlineShop.auth.controller;

import com.morashid.OnlineShop.auth.dto.AuthResponse;
import com.morashid.OnlineShop.auth.dto.LoginRequest;
import com.morashid.OnlineShop.auth.dto.RegisterRequest;
import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.auth.service.AuthService;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.user.dto.UserResponse;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * GET /api/auth/me
     * 
     * Inarudisha info ya user aliye-login.
     * 
     * @AuthenticationPrincipal - Spring Security ina-inject user aliye-login
     *   kutoka SecurityContext (ambayo JwtAuthenticationFilter ime-set).
     * 
     * Hii endpoint inahitaji token (si public).
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", response));
    }
}