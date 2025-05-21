package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.UserDto;

import java.util.List;

public interface UserFriendService {
    void followUser(Integer userId, Integer targetUserId);
    void unfollowUser(Integer userId, Integer targetUserId);
    List<UserDto> getFollowing(Integer userId);
    List<UserDto> getFollowers(Integer userId);
    boolean isFollowing(Integer userId, Integer targetUserId);
}