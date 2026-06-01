package com.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("edu_course")
public class EduCourse {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private Long teacherId;
    private Long departmentId;
    private BigDecimal credit;
    private String semester;
    private Integer classHours;
    private Integer capacity;
    private Integer enrolled;
    private String classroom;
    private String schedule;
    private String description;
    private Integer status;   // 0停开 1开设中 2已结课

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String teacherName;
    @TableField(exist = false)
    private String departmentName;
}
