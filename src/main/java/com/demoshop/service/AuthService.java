package com.demoshop.service;

import com.demoshop.config.JwtFilter;
import com.demoshop.domain.User;
import com.demoshop.dto.AuthDto;
import com.demoshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtFilter jwtFilter;

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setName(req.name());
        user.setRole(User.Role.USER);
        userRepository.save(user);
        String token = jwtFilter.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtFilter.generateToken(user.getEmail());
        return new AuthDto.AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
}
