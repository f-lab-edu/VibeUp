package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;

import java.util.List;

public record PostUpdateRequest(
    String caption, List<String> hashtags, MusicVendor vendor, String musicUrl) {}
