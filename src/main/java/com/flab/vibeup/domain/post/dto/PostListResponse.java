package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostListResponse(
        Long postId,
        String musicUrl,
        MusicVendor vendor,
        String caption,
        String userNickname,
        LocalDateTime createdAt
) {}
