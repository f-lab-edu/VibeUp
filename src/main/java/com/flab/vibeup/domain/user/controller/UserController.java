package com.flab.vibeup.domain.user.controller;

import com.flab.vibeup.domain.user.dto.UserResponse;
import com.flab.vibeup.domain.user.dto.UserSignupRequest;
import com.flab.vibeup.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody @Valid UserSignupRequest request) {
        UserResponse resp = userService.signup(request);
        return ResponseEntity.ok(resp);
    }
}
