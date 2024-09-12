package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.domain.UserStatus;
import com.lifestockserver.lifestock.user.dto.UserRegisterDto;
import com.lifestockserver.lifestock.user.mapper.UserMapper;
import com.lifestockserver.lifestock.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;  // MapStruct 주입

    @Override
    @Transactional
    public User registerMember(UserRegisterDto userRegisterDto) {
        User newUser = userMapper.toEntity(userRegisterDto);  // MapStruct로 매핑
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRole(UserRole.USER);  // 기본 역할 설정
        newUser.setStatus(UserStatus.ACTIVE);  // 기본 상태 설정
        return userRepository.save(newUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllMembers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findMemberById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findMemberByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public User updateMember(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        userRepository.deleteById(id);
    }
}
