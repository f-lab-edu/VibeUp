package com.flab.vibeup.domain.user.dto;

// 로그인 응답(액세스토큰 반환)
public record LoginResponse(String accessToken,long expiresInSeconds) {}