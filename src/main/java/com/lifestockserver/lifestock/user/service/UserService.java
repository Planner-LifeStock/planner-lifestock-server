package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserRegisterDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerMember(UserRegisterDto userRegisterDto);

    List<User> findAllMembers();

    Optional<User> findMemberById(Long id);

    Optional<User> findMemberByUsername(String username);

    User updateMember(User user);

    void deleteMember(Long id);
}
