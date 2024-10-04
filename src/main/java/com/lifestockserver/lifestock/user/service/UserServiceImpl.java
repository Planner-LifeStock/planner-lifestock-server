package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.mapper.UserMapper;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto registerUser(UserCreateDto userCreateDto) {
        User newUser = userMapper.toEntity(userCreateDto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(UserRole.USER);
        newUser.setStatus(UserStatus.ACTIVE);

        if (newUser.getDisplayName() == null || newUser.getDisplayName().isEmpty()) {
            newUser.setDisplayName(generateDisplayName(newUser));
        }

        return toResponseDto(userRepository.save(newUser));
    }

    private String generateDisplayName(User user) {
        String displayName;
        do {
            String realNameBased = user.getRealName().replaceAll("\\s", "_");
            displayName = realNameBased + "_" + new Random().nextInt(10000);
        } while (userRepository.findByDisplayName(displayName).isPresent());
        return displayName;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(u -> {
            u.setStatus(UserStatus.DELETED);
            userRepository.save(u);
        });
    }

    @Override
    public UserResponseDto toResponseDto(User user) {
        return userMapper.toDto(user);
    }
}
