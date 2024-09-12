package com.lifestockserver.lifestock.user.controller;

import com.lifestockserver.lifestock.user.service.UserService;
import com.lifestockserver.lifestock.user.domain.User;
import com.lifestockserver.lifestock.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/members")
    public String registerMember(@Valid UserRegisterDto userRegisterDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.registerMember(userRegisterDto);
        return "redirect:/members";
    }

    @GetMapping("/members")
    public String showMembers(Model model) {
        List<User> members = userService.findAllMembers();
        model.addAttribute("members", members);
        return "members";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }
}
