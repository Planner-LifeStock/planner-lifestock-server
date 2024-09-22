package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;  // MapStruct 주입

    @Override
    @Transactional
    public User registerUser(UserCreateDto userCreateDto) {
        User newUser = userMapper.toEntity(userCreateDto);  // MapStruct로 매핑
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(UserRole.USER);  // 기본 역할 설정
        newUser.setStatus(UserStatus.ACTIVE);  // 기본 상태 설정

        if (newUser.getDisplayName() == null || newUser.getDisplayName().isEmpty()) {
            newUser.setDisplayName(generateDisplayName(newUser));
        }

        return userRepository.save(newUser);
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
    public List<User> findAllUsers() {
        return userRepository.findAll();
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
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<User> findUserByIdResponse(Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findPaginatedUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public List<Integer> getPageNumbers(Page<User> userPage) {
        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
        }
        return List.of();
    }
}
