package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;
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
) {
    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .postId(post.getId())
                .musicUrl(post.getMusicUrl())
                .vendor(post.getVendor())
                .caption(post.getCaption())
                .userNickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
