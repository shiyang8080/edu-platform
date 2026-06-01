package com.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.dto.LoginDto;
import com.edu.dto.RegisterDto;
import com.edu.dto.UserQueryDto;
import com.edu.entity.SysUser;
import com.edu.mapper.SysUserMapper;
import com.edu.service.UserService;
import com.edu.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Result<?> login(LoginDto dto) {
        SysUser user = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null) {
            return Result.error(400, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用，请联系管理员");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("role", user.getRole());
        return Result.ok(data);
    }

    @Override
    public Result<?> register(RegisterDto dto) {
        SysUser exist = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (exist != null) {
            return Result.error(400, "用户名已存在");
        }
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setRole("STUDENT");
        user.setGender(dto.getGender() != null ? dto.getGender() : 0);
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(1);
        save(user);
        return Result.ok("注册成功");
    }

    @Override
    public SysUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        return getById(userId);
    }

    @Override
    public PageResult<SysUser> pageUsers(UserQueryDto query) {
        Page<SysUser> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(query.getUsername() != null, SysUser::getUsername, query.getUsername())
                .like(query.getRealName() != null, SysUser::getRealName, query.getRealName())
                .eq(query.getRole() != null, SysUser::getRole, query.getRole())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getCreateTime);
        page(page, wrapper);
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public Result<?> updateStatus(Long id, Integer status) {
        SysUser user = getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        updateById(user);
        return Result.ok();
    }
}
