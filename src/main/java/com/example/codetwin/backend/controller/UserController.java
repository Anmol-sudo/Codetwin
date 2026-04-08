package com.example.codetwin.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.codetwin.backend.dto.ApiResponse;
import com.example.codetwin.backend.dto.LoginRequest;
import com.example.codetwin.backend.dto.LoginResponse;
import com.example.codetwin.backend.dto.UserProfileResponse;
import com.example.codetwin.backend.dto.RefreshTokenRequest;
import com.example.codetwin.backend.dto.UserRegisterRequest;
import com.example.codetwin.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable Long id) {
        UserProfileResponse profile = userService.getUserProfile(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile loaded", profile));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody UserRegisterRequest request) {

        userService.registerUser(request);

        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                "User registered successfully",
                null
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.loginUser(
        request.getEmail(),
        request.getPassword()
        );

        return ResponseEntity.ok(
            new ApiResponse<>(
                true,
                "Login successful",
                response
            )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest request) {

        LoginResponse response = userService.refreshAccessToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    "Token refreshed successfully",
                    response
            )
        );
    }
    
}
