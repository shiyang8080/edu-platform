package com.edu.dto;

import lombok.Data;

@Data
public class UserQueryDto {
    private Integer page = 1;
    private Integer size = 10;
    private String username;
    private String realName;
    private String role;
    private Integer status;
}
