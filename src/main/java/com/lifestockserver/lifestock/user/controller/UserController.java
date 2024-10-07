package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import com.lifestockserver.lifestock.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 요청
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto userResponseDto = userService.registerUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    // 모든 유저 목록 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> showUsers() {
        List<UserResponseDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // 특정 유저 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 유저 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        UserResponseDto updatedUser = userService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    // 유저 삭제 (Status를 DELETED로 변경)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
