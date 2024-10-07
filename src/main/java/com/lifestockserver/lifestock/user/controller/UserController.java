package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.dto.UserUpdateDto;
import com.lifestockserver.lifestock.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 post 요청
    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDto userCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);  // 400 error
        }
        UserResponseDto userResponseDto = userService.registerUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);  // 201 Created
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
                .map(user -> ResponseEntity.ok(userService.toResponseDto(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    // 유저 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);  // 400 error
        }

        return userService.findUserById(id)
                .map(existingUser -> {
                    userUpdateDto.setId(id);
                    UserResponseDto updatedUser = userService.updateUser(userUpdateDto);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 유저 삭제 (Status를 DELETED로)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> {
                    userService.deleteUser(id);
                    return ResponseEntity.noContent().build();  // 204 No Content
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
