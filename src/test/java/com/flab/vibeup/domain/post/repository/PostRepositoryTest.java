package com.flab.vibeup.domain.post.repository;

import com.flab.vibeup.domain.post.entity.MusicVendor;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("userId 로 포스트 목록을 조회한다.")
    void findUserById() {
        //given
        User user = User.builder()
                .email("test@vibeup.com")
                .nickname("Jason")
                .password("encodedPw")
                .name("TestUser")
                .build();
        userRepository.save(user);

        Post post = Post.builder()
                .user(user)
                .vendor(MusicVendor.APPLE_MUSIC)
                .musicUrl("http://music.youtube.com")
                .caption("My Music")
                .hashtags(List.of("#감성", "테스트"))
                .build();
        postRepository.save(post);


        //when
        List<Post> posts = postRepository.findAll();

        //then
        assertThat(posts).hasSize(1);
        assertThat(posts.getFirst().getUser().getEmail()).isEqualTo("test@vibeup.com");
        assertThat(posts.getFirst().getVendor()).isEqualTo(MusicVendor.APPLE_MUSIC);
        assertThat(posts.getFirst().getMusicUrl()).isEqualTo("http://music.youtube.com");
    }
}
