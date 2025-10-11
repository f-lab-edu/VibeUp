package com.flab.vibeup.domain.post.controller;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.dto.PostListResponse;
import com.flab.vibeup.domain.post.dto.PostReadResponse;
import com.flab.vibeup.domain.post.dto.PostUpdateRequest;
import com.flab.vibeup.domain.post.service.PostService;
import com.flab.vibeup.global.auth.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final RedisTemplate<String, String> redisTemplate;

  @PostMapping
  public ResponseEntity<Void> createPost(
      @RequestBody @Valid PostCreateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    postService.createPost(request, userDetails.getUserId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{postId}")
  public ResponseEntity<PostReadResponse> readPost(@PathVariable Long postId) {
    PostReadResponse response = postService.readPost(postId);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<Page<PostListResponse>> getPostList(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    Page<PostListResponse> postList = postService.getPostList(pageable);
    return ResponseEntity.ok(postList);
  }

  @PutMapping("/{postId}")
  public ResponseEntity<Void> updatePost(
      @PathVariable Long postId,
      @RequestBody PostUpdateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails) {
    postService.updatePost(postId, request, userDetails.getUserId());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {
    postService.deletePost(postId, userDetails.getUserId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/feeds")
  public ResponseEntity<List<Long>> getFeeds(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getUserId();

    List<String> postIdStrings = redisTemplate.opsForList().range("feed:" + userId, 0, 9); // 최근 10개
    List<Long> postIds = Objects.requireNonNull(postIdStrings).stream().map(Long::valueOf).toList();

    return ResponseEntity.ok(postIds);
  }

  @GetMapping("/feeds/users/{userId}")
  public ResponseEntity<Page<PostListResponse>> getFeedsByUser(
      @PathVariable Long userId, Pageable pageable) {
    return ResponseEntity.ok(postService.getFeedsByUser(userId, pageable));
  }
}
