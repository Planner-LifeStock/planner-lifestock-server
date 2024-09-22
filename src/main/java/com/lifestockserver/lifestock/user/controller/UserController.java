package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.dto.UserCreateDto;
import com.lifestockserver.lifestock.user.service.UserService;
import com.lifestockserver.lifestock.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 처리
    @PostMapping
    public String registerUser(@Valid UserCreateDto userCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.registerUser(userCreateDto);
        return "redirect:/users";
    }

    // 모든 유저 목록 조회
    @GetMapping
    public String showUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size, Model model) {
        Page<User> userPage = userService.findPaginatedUsers(page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        List<Integer> pageNumbers = userService.getPageNumbers(userPage);
        model.addAttribute("pageNumbers", pageNumbers);

        return "users";
    }

    // 특정 유저 조회
    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        userService.findUserById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user";
    }

    // 회원가입 폼
    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }
}
