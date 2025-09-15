package com.flab.vibeup.domain.user.repository;

import com.flab.vibeup.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 로그인 이메일 검증용
}