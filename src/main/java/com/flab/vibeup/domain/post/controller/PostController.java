package com.flab.vibeup.domain.post.controller;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.dto.PostReadResponse;
import com.flab.vibeup.domain.post.service.PostService;
import com.flab.vibeup.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody @Valid PostCreateRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.createPost(request, userDetails.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostReadResponse> readPost(@PathVariable Long postId) {
        PostReadResponse response = postService.readPost(postId);
        return ResponseEntity.ok(response);
    }
}
