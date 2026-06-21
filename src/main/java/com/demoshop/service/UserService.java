package com.demoshop.service;

import com.demoshop.domain.User;
import com.demoshop.dto.UserDto;
import com.demoshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto.UserResponse getProfile(User user) {
        return toResponse(user);
    }

    @Transactional
    public UserDto.UserResponse updateProfile(User user, UserDto.UpdateRequest req) {
        if (!user.getEmail().equals(req.email()) && userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        user.setName(req.name());
        user.setEmail(req.email());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserDto.UserResponse topUp(User user, UserDto.TopUpRequest req) {
        user.setBalance(user.getBalance().add(req.amount()));
        return toResponse(userRepository.save(user));
    }

    public static UserDto.UserResponse toResponse(User u) {
        return new UserDto.UserResponse(u.getId(), u.getEmail(), u.getName(),
                u.getRole().name(), u.getBalance(), u.getCreatedAt());
    }
}
