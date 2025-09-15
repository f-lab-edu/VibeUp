package com.flab.vibeup.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.flab.vibeup.auth.JwtTokenProvider;
import com.flab.vibeup.domain.user.dto.LoginRequest;
import com.flab.vibeup.domain.user.dto.LoginResponse;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks AuthService authService;

    @Test
    void login_success_returns_token() {
        var request = new LoginRequest("hyeonsuns@kakao.com", "pw1234!");
        var user = User.builder()
                .id(1L)
                .email(request.email())
                .password("pw1234!")
                .nickname("Hyeonsun Jung")
                .build();
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createToken(eq(user.getEmail()), anyMap())).willReturn("JWT-TOKEN"); //user email 전달

        LoginResponse res = authService.login(request);

        then(userRepository).should().findByEmail(request.email());
        then(passwordEncoder).should().matches(request.password(), user.getPassword());
        then(jwtTokenProvider).should().createToken(eq(user.getEmail()), anyMap());

        assertThat(res.accessToken()).isEqualTo("JWT-TOKEN"); // 토큰은 반드시 JWT-TOKEN으로 발급 리턴
    }

    @Test
    void login_fail_wrong_password() {
        var request = new LoginRequest("hyeonsuns@kakao.com", "pw1234!");
        var user = User.builder()
                .id(1L)
                .email(request.email())
                .password("pw1234!")
                .nickname("Hyeonsun Jung")
                .build();
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.password(), user.getPassword())).willReturn(false); // 비번 틀림

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class); // 실제 IllegalArgumentException 이 발생하는지 확인
        then(jwtTokenProvider).shouldHaveNoInteractions(); // 패스워드가 다르면 토큰발행이 되면 안됨
    }
}
