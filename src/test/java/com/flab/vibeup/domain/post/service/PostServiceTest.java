package com.flab.vibeup.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.dto.PostListResponse;
import com.flab.vibeup.domain.post.dto.PostReadResponse;
import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@Transactional
class PostServiceTest {

  @Autowired private PostService postService;
  @Autowired private PostRepository postRepository;
  @Autowired private UserRepository userRepository;

  private User savedUser;

  @BeforeEach
  void setUp() {
    postRepository.deleteAllInBatch(); // 빠르고 깔끔하게 삭제
    userRepository.deleteAllInBatch();

    User user =
        User.builder()
            .email("test@example.com")
            .password("secure1234")
            .name("Hyeonsun Jung")
            .nickname("testUser")
            .build();
    savedUser = userRepository.save(user);
  }

  @Test
  @DisplayName("포스트 생성 요청 시, 게시글이 저장되고 userId가 반환된다")
  void createPost_shouldSavePostAndReturnUserId() {
    // given
    PostCreateRequest request =
        new PostCreateRequest(
            MusicVendor.YOUTUBE_MUSIC,
            "https://youtube.com/testmusic",
            "이 노래 추천!!",
            List.of("감성", "추천"));

    // when
    Long savedPostId = postService.createPost(request, savedUser.getId());
    Post savedPost =
        postRepository
            .findById(savedPostId)
            .orElseThrow(() -> new IllegalStateException("Post not found"));

    // then
    assertThat(savedPost).isNotNull();
    assertThat(savedPost.getUser().getId()).isEqualTo(savedUser.getId());
    assertThat(savedPost.getMusicUrl()).isEqualTo(request.musicUrl());
    assertThat(savedPost.getHashtags()).containsExactly("감성", "추천");
    assertThat(savedPost.getVendor()).isEqualTo(MusicVendor.YOUTUBE_MUSIC);
  }

  @Test
  @DisplayName("포스트 ID로 조회 시, 해당 게시글이 반환된다")
  void getPostById_shouldReturnPost() {
    // given
    PostCreateRequest request =
        new PostCreateRequest(
            MusicVendor.YOUTUBE_MUSIC,
            "https://youtube.com/testmusic2",
            "또 다른 노래 추천!",
            List.of("드라이브", "신나는"));

    Long savedPostId = postService.createPost(request, savedUser.getId());

    // when
    PostReadResponse foundPost = postService.readPost(savedPostId);

    // then
    assertThat(foundPost).isNotNull();
    assertThat(foundPost.postId()).isEqualTo(savedPostId);
    assertThat(foundPost.musicUrl()).isEqualTo(request.musicUrl());
    assertThat(foundPost.vendor()).isEqualTo(request.vendor());
    assertThat(foundPost.hashtags()).containsExactlyElementsOf(request.hashtags());
    assertThat(foundPost.userId()).isEqualTo(savedUser.getId());
  }

  @Test
  @DisplayName("페이지네이션으로 게시글 목록을 조회할 수 있다")
  void getPostsWithPagination_shouldReturnPagedPosts() {
    // given
    for (int i = 1; i <= 25; i++) {
      PostCreateRequest request =
          new PostCreateRequest(
              MusicVendor.YOUTUBE_MUSIC,
              "https://youtube.com/testmusic" + i,
              "노래 추천 " + i,
              List.of("태그" + i));
      postService.createPost(request, savedUser.getId());
    }

    Pageable pageable = PageRequest.of(0, 10);

    // when
    Page<PostListResponse> postPage = postService.getPostList(pageable);

    // then
    assertThat(postPage.getContent()).hasSize(10);
    assertThat(postPage.getTotalElements()).isEqualTo(25);
    assertThat(postPage.getTotalPages()).isEqualTo(3);
    assertThat(postPage.getNumber()).isEqualTo(0);
    assertThat(postPage.getContent().getFirst().musicUrl())
        .startsWith("https://youtube.com/testmusic");
  }
}
