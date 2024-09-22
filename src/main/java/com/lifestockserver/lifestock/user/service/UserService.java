package com.lifestockserver.lifestock.user.service;

import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerMember(UserCreateDto userCreateDto);

    List<User> findAllMembers();

    Optional<User> findMemberById(Long id);

    Optional<User> findMemberByUsername(String username);

    User updateMember(User user);

    void deleteMember(Long id);

    ResponseEntity<User> findMemberByIdResponse(Long id);

    Page<User> findAllMembers(Pageable pageable);

    Page<User> findPaginatedMembers(int page, int size);

    List<Integer> getPageNumbers(Page<User> userPage);
}
