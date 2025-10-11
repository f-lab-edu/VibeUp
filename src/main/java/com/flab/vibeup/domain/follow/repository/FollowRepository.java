package com.flab.vibeup.domain.follow.repository;

import com.flab.vibeup.domain.follow.entity.Follow;
import com.flab.vibeup.domain.user.entity.User;

import java.util.List;

public interface FollowRepository {
    List<Follow> findAllByFollowee(User followee);
}