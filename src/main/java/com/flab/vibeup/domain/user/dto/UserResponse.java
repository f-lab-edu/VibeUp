package com.flab.vibeup.domain.user.dto;

public record UserResponse(
        Long id,
        String email,
        String username,
        String nickname
) {}