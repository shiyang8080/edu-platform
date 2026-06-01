package com.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_notice_read")
public class SysNoticeRead {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long noticeId;
    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime readTime;
}
