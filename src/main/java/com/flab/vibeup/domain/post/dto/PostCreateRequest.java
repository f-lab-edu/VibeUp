package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;

import java.util.List;

public record PostCreateRequest(
        MusicVendor vendor,
        String musicUrl,
        String caption,
        List<String> hashtags
) {}
