package com.flab.vibeup.domain.post.service;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.dto.PostListResponse;
import com.flab.vibeup.domain.post.dto.PostReadResponse;
import com.flab.vibeup.domain.post.dto.PostUpdateRequest;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  @Transactional
  public Long createPost(PostCreateRequest request, Long userId) {
    User user = User.builder().id(userId).build();

    Post post =
        Post.builder()
            .user(user)
            .musicUrl(request.musicUrl())
            .vendor(request.vendor())
            .caption(request.caption())
            .hashtags(request.hashtags())
            .build();

    Post savedPost = postRepository.save(post);
    return savedPost.getId();
  }

  @Transactional(readOnly = true)
  public PostReadResponse readPost(Long postId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

    return PostReadResponse.from(post);
  }

  @Transactional(readOnly = true)
  public Page<PostListResponse> getPostList(Pageable pageable) {
    return postRepository
        .findAll(pageable)
        .map(
            post ->
                PostListResponse.builder()
                    .postId(post.getId())
                    .musicUrl(post.getMusicUrl())
                    .vendor(post.getVendor())
                    .caption(post.getCaption())
                    .userNickname(post.getUser().getNickname())
                    .createdAt(post.getCreatedAt())
                    .build());
  }

  @Transactional
  public void updatePost(Long postId, PostUpdateRequest request, Long userId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    // 작성자 본인만 수정 가능
    if (!post.getUser().getId().equals(userId)) {
      throw new SecurityException("작성자만 수정할 수 있습니다.");
    }

    post.update(request.caption(), request.hashtags(), request.vendor(), request.musicUrl());
  }

  @Transactional
  public void deletePost(Long postId, Long userId) {
    Post post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(userId)) {
      throw new SecurityException("작성자만 삭제할 수 있습니다.");
    }
    postRepository.delete(post);
  }

  @Transactional(readOnly = true)
  public Page<PostListResponse> getFeeds(Pageable pageable) {
    return postRepository.findAllByOrderByCreatedAtDesc(pageable).map(PostListResponse::from);
  }

  @Transactional(readOnly = true)
  public Page<PostListResponse> getFeedsByUser(Long userId, Pageable pageable) {
    return postRepository
        .findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
        .map(PostListResponse::from);
  }
}
