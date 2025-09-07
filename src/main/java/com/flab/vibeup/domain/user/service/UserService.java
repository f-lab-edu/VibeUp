package com.flab.vibeup.domain.user.service;

import com.flab.vibeup.domain.user.dto.UserResponse;
import com.flab.vibeup.domain.user.dto.UserSignupRequest;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(UserSignupRequest req) {
        //중복 체크 & 비즈니스로직
        userRepository.findByEmail(req.email())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 사용중인 이메일입니다."); });
        userRepository.findByNickname(req.nickname())
                .ifPresent(u -> { throw new IllegalArgumentException("이미 사용중인 닉네임입니다."); } );
        String hashed = passwordEncoder.encode(req.password());
        User user = User.builder()
                .email(req.email())
                .name(req.username())
                .password(hashed)
                .nickname(req.nickname())
                .build();
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getName(), saved.getNickname());
    }
}