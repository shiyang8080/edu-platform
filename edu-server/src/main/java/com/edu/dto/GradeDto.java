package com.edu.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GradeDto {
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    private BigDecimal regularScore;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    private String remark;
}
