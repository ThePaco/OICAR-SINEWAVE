package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.exception.ResourceNotFoundException;
import hr.spring.web.sinewave.exception.UnauthorizedException;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.model.Userfriend;
import hr.spring.web.sinewave.model.UserfriendId;
import hr.spring.web.sinewave.repository.UserFriendRepository;
import hr.spring.web.sinewave.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFriendServiceImpl implements UserFriendService {

    private final UserRepository userRepository;
    private final UserFriendRepository userFriendRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserFriendServiceImpl(UserRepository userRepository,
                                 UserFriendRepository userFriendRepository,
                                 ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.userFriendRepository = userFriendRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void followUser(Integer userId, Integer targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new UnauthorizedException("You cannot follow yourself");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        if (userFriendRepository.existsByUseridAndFriendid(user, targetUser)) {
            throw new UnauthorizedException("You are already following this user");
        }

        UserfriendId friendId = new UserfriendId();
        friendId.setUserid(userId);
        friendId.setFriendid(targetUserId);

        Userfriend friendship = new Userfriend();
        friendship.setId(friendId);
        friendship.setUserid(user);
        friendship.setFriendid(targetUser);
        friendship.setAddedat(Instant.now());

        userFriendRepository.save(friendship);
    }

    @Override
    public void unfollowUser(Integer userId, Integer targetUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        Userfriend friendship = userFriendRepository.findByUseridAndFriendid(user, targetUser)
                .orElseThrow(() -> new ResourceNotFoundException("You are not following this user"));

        userFriendRepository.delete(friendship);
    }

    @Override
    public List<UserDto> getFollowing(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userFriendRepository.findByUserid(user).stream()
                .map(friendship -> modelMapper.map(friendship.getFriendid(), UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getFollowers(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return userFriendRepository.findByFriendid(user).stream()
                .map(friendship -> modelMapper.map(friendship.getUserid(), UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isFollowing(Integer userId, Integer targetUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found with id: " + targetUserId));

        return userFriendRepository.existsByUseridAndFriendid(user, targetUser);
    }
}