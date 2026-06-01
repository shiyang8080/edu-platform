package com.edu.dto;

import lombok.Data;

@Data
public class CourseQueryDto {
    private Integer page = 1;
    private Integer size = 10;
    private String name;
    private String code;
    private Long teacherId;
    private Long departmentId;
    private String semester;
    private Integer status;
}
