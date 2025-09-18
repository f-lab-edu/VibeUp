package com.flab.vibeup.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.vibeup.domain.user.entity.User;
import com.flab.vibeup.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void 로그인_성공_후_토큰으로_사용자정보_조회() throws Exception {
        // 사용자 등록
        User user = new User(
                null,
                "jason@vibeup.com",
                "jason123",
                passwordEncoder.encode("pw1234"),
                "Hyeonsun Jung",
                null,
                null
        );
        userRepository.save(user);

        // 로그인 요청
        String loginPayload = objectMapper.writeValueAsString(Map.of(
                "email", "jason@vibeup.com",
                "password", "pw1234"
        ));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("accessToken").asText();

        // 토큰을 이용해 인증 API 호출
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jason@vibeup.com"));
    }

    @WithAnonymousUser
    @Test
    void 로그인_실패_비밀번호_틀림() throws Exception {
        // Given
        User user = new User(
                null,
                "jason@vibeup.com",
                "jason123",
                passwordEncoder.encode("pw1234"),
                "Hyeonsun Jung",
                null,
                null
        );
        userRepository.save(user);

        // When
        String payload = objectMapper.writeValueAsString(Map.of(
                "email", "jason@vibeup.com",
                "password", "wrongpw"
        ));

        // Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}