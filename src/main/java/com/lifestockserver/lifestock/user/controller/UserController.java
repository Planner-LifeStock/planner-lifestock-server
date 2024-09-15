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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/members")
    public String registerMember(@Valid UserCreateDto userCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.registerMember(userCreateDto);
        return "redirect:/members";
    }

    @GetMapping("/members")
    public String showMembers(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size, Model model) {
        Page<User> userPage = userService.findPaginatedMembers(page, size);

        model.addAttribute("members", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());

        List<Integer> pageNumbers = userService.getPageNumbers(userPage);
        model.addAttribute("pageNumbers", pageNumbers);

        return "members";
    }

    @GetMapping("/members/{id}")
    public String getMemberById(@PathVariable Long id, Model model) {
        userService.findMemberById(id).ifPresent(user -> model.addAttribute("user", user));
        return "member";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }
}
