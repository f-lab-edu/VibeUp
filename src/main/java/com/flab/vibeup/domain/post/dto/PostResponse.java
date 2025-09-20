package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String nickname,
        MusicVendor vendor,
        String musicUrl,
        String caption,
        List<String> hashtags,
        LocalDateTime createdAt
) {}