package com.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("edu_selection")
public class EduSelection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long courseId;
    private Integer status;   // 0退课 1在修 2已修完

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime selectTime;

    @TableField(exist = false)
    private String studentName;
    @TableField(exist = false)
    private String courseName;
}
