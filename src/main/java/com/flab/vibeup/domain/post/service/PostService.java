
package com.flab.vibeup.domain.post.service;

import com.flab.vibeup.domain.post.dto.PostCreateRequest;
import com.flab.vibeup.domain.post.entity.Post;
import com.flab.vibeup.domain.post.repository.PostRepository;
import com.flab.vibeup.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long createPost(PostCreateRequest request, Long userId) {
        User user = User.builder().id(userId).build();

        Post post = Post.builder()
                .user(user)
                .musicUrl(request.musicUrl())
                .vendor(request.vendor())
                .caption(request.caption())
                .hashtags(request.hashtags())
                .build();

        Post savedPost = postRepository.save(post);
        return savedPost.getId();
    }
}
