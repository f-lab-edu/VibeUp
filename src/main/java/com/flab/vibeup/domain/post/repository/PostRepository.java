package com.flab.vibeup.domain.post.repository;

import com.flab.vibeup.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserId(Long userId);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable); // 최신순 피드 조회

    Page<Post> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable); // 특정 유저의 피드 조회
}
