package com.example.codetwin.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.codetwin.backend.config.JwtUtil;
import com.example.codetwin.backend.dto.LoginResponse;
import com.example.codetwin.backend.dto.UserProfileResponse;
import com.example.codetwin.backend.dto.UserRegisterRequest;
import com.example.codetwin.backend.model.RefreshToken;
import com.example.codetwin.backend.model.Role;
import com.example.codetwin.backend.model.User;
import com.example.codetwin.backend.repository.UserRepository;
import com.example.codetwin.backend.repository.PostRepository;

@Service
public class UserService {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        long postCount = postRepository.countByUser(user);
        
        return new UserProfileResponse(
            user.getId(),
            user.getActualUsername(),
            user.getEmail(),
            postCount
        );
    }

    public String registerUser(UserRegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setRole(Role.USER);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); 

        userRepository.save(user);

        return "User registered successfully";
    }

    public LoginResponse loginUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 1. Generate access token
        String accessToken = jwtUtil.generateToken(user);

        // 2. Create refresh token (this already deletes old ones internally ✅)
        String refreshToken = refreshTokenService
                .createRefreshToken(user.getEmail())
                .getToken();

        // 3. Return both
        return new LoginResponse(accessToken, refreshToken, user.getId());
    }

    public LoginResponse refreshAccessToken(String requestToken) {

        // 1. Find token
        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken);

        // 2. Verify expiry
        refreshTokenService.verifyExpiration(refreshToken);

        // 3. Get user
        User user = refreshToken.getUser();

        // 4. Generate new access token
        String accessToken = jwtUtil.generateToken(user);

        return new LoginResponse(accessToken, requestToken, user.getId());
    }
}
