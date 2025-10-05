package com.flab.vibeup.domain.post.service;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.dto.PostReadResponse;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

    return PostReadResponse.from(post);
  }
}
