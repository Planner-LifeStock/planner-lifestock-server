package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.domain.UserRole;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserResponseDto registerUser(UserCreateDto userCreateDto);

    List<UserResponseDto> findAllUsers();

    Optional<UserResponseDto> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    void deleteUser(Long id);

    UserResponseDto toResponseDto(User user);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto);

    UserRole getUserRoleByUsername(String username);
}
