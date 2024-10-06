package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.dto.UserResponseDto;
import com.lifestockserver.lifestock.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 post 요청
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDto userCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);  // 400 error
        }

        UserResponseDto userResponseDto = userService.registerUser(userCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);  // 201 Created
    }

    /* 회원가입 폼(없어도 될 거 같은데 실제로 괜찮으면 지울게요)
    @GetMapping("/register")
    @ResponseBody  // JSON 응답
    public ResponseEntity<UserCreateDto> showRegistrationForm() {
        UserCreateDto userCreateDto = new UserCreateDto();
        return ResponseEntity.ok(userCreateDto);  // 200 OK와 함께 기본 UserCreateDto 반환
    }
    */

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
}
