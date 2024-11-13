package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import com.lifestockserver.lifestock.user.mapper.UserMapper;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import com.lifestockserver.lifestock.user.domain.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
            displayName = realNameBased + "_" + ThreadLocalRandom.current().nextInt(10000);
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
    public Optional<UserResponseDto> findUserById(Long id) {
        return userRepository.findById(id).map(this::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserByIdOrThrow(id);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = findUserByIdOrThrow(id);

        user.setRealName(userUpdateDto.getRealName());
        user.setDisplayName(userUpdateDto.getDisplayName());
        user.setEmail(userUpdateDto.getEmail());
        user.setPhoneNumber(userUpdateDto.getPhoneNumber());

        return toResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto toResponseDto(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new CustomUserDetails(
                        user.getUsername(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority(user.getRole().name())),
                        user.getId()
                    )
                )   
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserRole getUserRoleByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getRole)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다: " + username));
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional
    public void updateUserAsset(Long id, Long amount) {
        User user = findUserByIdOrThrow(id);
        user.setAsset(user.getAsset() + amount);
        userRepository.save(user);
    }
}
