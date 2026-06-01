package com.edu.controller;

import com.edu.common.Result;
import com.edu.dto.UserQueryDto;
import com.edu.entity.SysUser;
import com.edu.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> list(UserQueryDto query) {
        return Result.ok(userService.pageUsers(query));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> detail(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> create(@RequestBody SysUser user) {
        SysUser exist = userService.getOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, user.getUsername()));
        if (exist != null) {
            return Result.error(400, "用户名已存在");
        }
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        SysUser dbUser = userService.getById(id);
        if (dbUser == null) return Result.error("用户不存在");
        user.setId(id);
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(dbUser.getPassword());
        }
        user.setCreateTime(dbUser.getCreateTime());
        userService.updateById(user);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return userService.updateStatus(id, status);
    }
}
