package com.flab.vibeup.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 3, max = 20) String username,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank String nickname
) {}