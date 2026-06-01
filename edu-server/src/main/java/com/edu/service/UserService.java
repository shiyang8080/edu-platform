package com.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.dto.LoginDto;
import com.edu.dto.RegisterDto;
import com.edu.dto.UserQueryDto;
import com.edu.entity.SysUser;

public interface UserService extends IService<SysUser> {
    Result<?> login(LoginDto loginDto);
    Result<?> register(RegisterDto registerDto);
    SysUser getCurrentUser();
    PageResult<SysUser> pageUsers(UserQueryDto query);
    Result<?> updateStatus(Long id, Integer status);
}
