package com.edu.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩视图对象 — 合并学生选课信息与成绩信息
 */
@Data
public class GradeVO {
    // 选课信息
    private Long selectionId;
    private Long studentId;
    private Long courseId;

    // 学生信息
    private String studentName;
    private String username;

    // 成绩信息 (可能为空，表示尚未录入)
    private Long gradeId;
    private BigDecimal regularScore;
    private BigDecimal midtermScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;
    private BigDecimal gradePoint;
    private String gradeLevel;
    private String remark;

    // 记录时间
    private LocalDateTime recordTime;
    private LocalDateTime updateTime;
}
