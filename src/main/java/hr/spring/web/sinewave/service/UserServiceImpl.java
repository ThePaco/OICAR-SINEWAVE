package hr.spring.web.sinewave.service;

import hr.spring.web.sinewave.dto.UserCreateDto;
import hr.spring.web.sinewave.dto.UserDto;
import hr.spring.web.sinewave.model.User;
import hr.spring.web.sinewave.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Integer id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserDto.class))
                .orElse(null);
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        User user = modelMapper.map(dto, User.class);

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        user.setPasswordhash(hashedPassword);

        System.out.println("🛠 USER BEFORE SAVE:");
        System.out.println("ID: " + user.getId());
        System.out.println("Username: " + user.getUsername());

        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserDto.class);
    }

    @Override
    public UserDto update(Integer id, UserCreateDto dto) {
        return userRepository.findById(id)
                .map(existing -> {
                    modelMapper.map(dto, existing);

                    String hashed = passwordEncoder.encode(dto.getPassword());
                    existing.setPasswordhash(hashed);

                    User updated = userRepository.save(existing);
                    return modelMapper.map(updated, UserDto.class);
                })
                .orElse(null);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
}
