package com.edu.controller;

import com.edu.common.Result;
import com.edu.dto.LoginDto;
import com.edu.dto.RegisterDto;
import com.edu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDto dto) {
        return userService.login(dto);
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDto dto) {
        return userService.register(dto);
    }

    @GetMapping("/info")
    public Result<?> info() {
        return Result.ok(userService.getCurrentUser());
    }
}
