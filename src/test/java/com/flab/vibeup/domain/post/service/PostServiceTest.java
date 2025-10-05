package com.flab.vibeup.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class PostServiceTest {

  @Autowired private PostService postService;

  @Autowired private PostRepository postRepository;

  @Autowired private UserRepository userRepository;

  @Test
  @DisplayName("포스트 생성 요청 시, 게시글이 저장되고 userId가 반환된다")
  void createPost_shouldSavePostAndReturnUserId() {
    // given
    User user =
        User.builder()
            .email("test@example.com")
            .password("secure1234")
            .name("Jason")
            .nickname("testUser")
            .build();
    userRepository.save(user);

    PostCreateRequest request =
        new PostCreateRequest(
            MusicVendor.YOUTUBE_MUSIC,
            "https://youtube.com/testmusic",
            "이 노래 추천!!",
            List.of("감성", "추천"));

    // when
    Long savedPostId = postService.createPost(request, user.getId());
    Post savedPost = postRepository.findById(savedPostId)
            .orElseThrow(() -> new IllegalStateException("Post not found"));

    // then
    assertThat(savedPost).isNotNull();
    assertThat(user.getId()).isEqualTo(savedPost.getUser().getId());
    assertThat(savedPost.getMusicUrl()).isEqualTo(request.musicUrl());
    assertThat(savedPost.getHashtags()).containsExactly("감성", "추천");
    assertThat(savedPost.getVendor()).isEqualTo(MusicVendor.YOUTUBE_MUSIC);
  }
}
