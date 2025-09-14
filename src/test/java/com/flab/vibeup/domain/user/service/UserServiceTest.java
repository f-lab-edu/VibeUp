package com.flab.vibeup.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.flab.vibeup.domain.user.dto.UserSignupRequest;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserService userService;

    private UserSignupRequest req() {
        return new UserSignupRequest(
                "hyeonsuns@kakao.com",
                "Hyeonsun Jung",
                "password1234!",
                "HS JUNG"
        );
    }

    @Test
    @DisplayName("정상 회원가입 테스트 : 새로운 이메일/닉네임이면 신규저장, 비밀번호는 인코딩")
    void signup_success() {
        var request = req();
        // given
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        var saved = User.builder()
                .id(1L)
                .email(request.email())
                .nickname(request.nickname())
                .password("encoded") // password encoded mock 처리
                .build();
        given(userRepository.save(any(User.class))).willReturn(saved); // save 객체를 DB적재 하지 않고 그대로 리턴

        // when
        var resp = userService.signup(request);

        // then
        then(passwordEncoder).should().encode(request.password());
        then(userRepository).should().save(any(User.class));
        assertThat(resp.id()).isEqualTo(1L);
        assertThat(resp.email()).isEqualTo("hyeonsuns@kakao.com");
        assertThat(resp.nickname()).isEqualTo("HS JUNG");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 : 이메일 / 닉네임 중복저장")
    void signup_fail_when_unique_constraint_violated() {
        //given
        var request = req();
        given(passwordEncoder.encode(anyString())).willReturn("encoded");
        //저장할 때 DB 제약위반 상황 가정
        given(userRepository.save(any(User.class)))
                .willThrow(new DataIntegrityViolationException("Unique Constraint"));

        //when, then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(DataIntegrityViolationException.class);
        then(passwordEncoder).should().encode(request.password());
        then(userRepository).should().save(any(User.class));
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    @DisplayName("회원가입 실패 테스트 : 비밀번호 암호화 실패")
    void signup_fail_password_encoding() {
        //given
        var request = req();
        given(passwordEncoder.encode(anyString()))
                .willThrow(new IllegalStateException("encoder fail"));

        //when, then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("encoder fail");

        // save가 되지 않아야함
        then(userRepository).shouldHaveNoInteractions();
    }
}