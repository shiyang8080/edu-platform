package com.edu.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class NoticeDto {
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "内容不能为空")
    private String content;
    private String noticeType = "GENERAL";
    private Integer priority = 0;
    private String targetRole = "ALL";
}
