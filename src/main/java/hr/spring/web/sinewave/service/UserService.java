package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.dto.UserLoginDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    UserDto getById(Integer id);
    UserDto create(UserCreateDto dto);
    UserDto update(Integer id, UserCreateDto dto);
    void delete(Integer id);
    UserDto authenticateUser(UserLoginDto dto);
    List<UserDto> searchByUsername(String username);
    void anonymizeUser(Integer userId);
}