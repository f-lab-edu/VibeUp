package com.flab.vibeup.domain.post.service;

import com.flab.vibeup.domain.follow.entity.Follow;
import com.flab.vibeup.domain.follow.repository.FollowRepository;
import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceFanoutTest {
  @Mock private PostRepository postRepository;
  @Mock private FollowRepository followRepository;

  // Redis
  @Mock private RedisTemplate<String, String> redisTemplate;
  @Mock private ListOperations<String, String> listOperations;

  @InjectMocks private PostService postService;

  private User author;
  private User follower1;
  private User follower2;

  @BeforeEach
  void setUp() {
    author = User.builder().id(10L).email("author@ex.com").build();
    follower1 = User.builder().id(20L).email("f1@ex.com").build();
    follower2 = User.builder().id(30L).email("f2@ex.com").build();
  }

  @Test
  @DisplayName("포스트 작성 시, 팔로워들의 Redis feed에 postId를 push 한다")
  void createPost_shouldPushPostIdToFollowersFeed() {
    when(redisTemplate.opsForList()).thenReturn(listOperations);

    // given: 요청 데이터
    PostCreateRequest request =
        new PostCreateRequest(
            MusicVendor.YOUTUBE_MUSIC, "https://music.example/song1", "좋은곡", List.of("#tag"));

    // 모의 저장될 Post (save 후 id가 세팅되어 반환된다고 가정)
    Post savedPost =
        Post.builder()
            .id(100L) // save 이후 id
            .user(author)
            .musicUrl(request.musicUrl())
            .vendor(request.vendor())
            .caption(request.caption())
            .hashtags(request.hashtags())
            .build();

    // postRepository.save(...) 호출 시 savedPost 반환
    when(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

    // followRepository.findAllByFollowee(author) -> 두 팔로워 반환
    Follow f1 = Follow.builder().follower(follower1).followee(author).build();
    Follow f2 = Follow.builder().follower(follower2).followee(author).build();
    when(followRepository.findAllByFollowee(any(User.class))).thenReturn(List.of(f1, f2));

    // when
    Long resultId = postService.createPost(request, author.getId());

    // then
    // 반환된 id가 저장된 id인지 확인
    org.assertj.core.api.Assertions.assertThat(resultId).isEqualTo(100L);

    // Redis에 각 팔로워 키로 leftPush 호출됐는지 검증
    // key = "feed:{followerId}", value = savedPost.getId().toString()
    verify(listOperations, times(1)).leftPush(eq("feed:" + follower1.getId()), eq("100"));
    verify(listOperations, times(1)).leftPush(eq("feed:" + follower2.getId()), eq("100"));
  }

  @Test
  @DisplayName("팔로워가 없으면 Redis push가 호출되지 않는다")
  void createPost_whenNoFollowers_shouldNotPushToRedis() {
    // given
    PostCreateRequest request =
        new PostCreateRequest(
            MusicVendor.YOUTUBE_MUSIC, "https://music.example/song2", "곡2", List.of("#t"));

    Post savedPost = Post.builder().id(101L).user(author).build();
    when(postRepository.save(org.mockito.ArgumentMatchers.any(Post.class))).thenReturn(savedPost);

    when(followRepository.findAllByFollowee(any(User.class))).thenReturn(List.of());

    // when
    Long resultId = postService.createPost(request, author.getId());

    // then
    org.assertj.core.api.Assertions.assertThat(resultId).isEqualTo(101L);
    // listOperations.leftPush가 호출되지 않아야 함
    verify(listOperations, times(0)).leftPush(anyString(), anyString());
  }
}
