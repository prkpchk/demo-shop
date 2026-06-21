package com.demoshop.controller;

import com.demoshop.domain.User;
import com.demoshop.dto.UserDto;
import com.demoshop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto.UserResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto.UserResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserDto.UpdateRequest req) {
        return ResponseEntity.ok(userService.updateProfile(user, req));
    }

    @PostMapping("/me/top-up")
    public ResponseEntity<UserDto.UserResponse> topUp(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserDto.TopUpRequest req) {
        return ResponseEntity.ok(userService.topUp(user, req));
    }
}
