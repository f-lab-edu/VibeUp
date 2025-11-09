package com.flab.vibeup.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    @Test
    void redisPushAndPullTest() {
        redisTemplate.opsForList().leftPush("feed:1", 100L);
        Long postId = redisTemplate.opsForList().leftPop("feed:1");
        assertThat(postId).isEqualTo(100L);
    }
}
