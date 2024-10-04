package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponseDto registerUser(UserCreateDto userCreateDto);

    List<UserResponseDto> findAllUsers();

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    User updateUser(User user);

    void deleteUser(Long id);

    UserResponseDto toResponseDto(User user);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
