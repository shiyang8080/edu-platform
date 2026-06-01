package com.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice")
public class SysNotice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private String noticeType;  // SYSTEM, COURSE, GRADE, GENERAL
    private Integer priority;   // 0普通 1重要 2紧急
    private String targetRole;  // ALL, STUDENT, TEACHER
    private Integer status;     // 0草稿 1已发布 2已撤回

    private LocalDateTime publishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String publisherName;
}
