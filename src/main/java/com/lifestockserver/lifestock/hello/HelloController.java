package com.lifestockserver.lifestock.hello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping
    public String hello() {
        log.info("Hello, World!");
        return "Hello, World!";
    }
}
