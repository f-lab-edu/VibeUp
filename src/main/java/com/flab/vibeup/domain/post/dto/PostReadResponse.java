package com.flab.vibeup.domain.post.dto;

import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;

import java.util.List;

public record PostReadResponse(
    Long postId,
    String musicUrl,
    MusicVendor vendor,
    String caption,
    List<String> hashtags,
    Long userId) {
  public static PostReadResponse from(Post post) {
    return new PostReadResponse(
        post.getId(),
        post.getMusicUrl(),
        post.getVendor(),
        post.getCaption(),
        post.getHashtags(),
        post.getUser().getId());
  }
}
