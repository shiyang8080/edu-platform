package com.edu.service;

import com.edu.dto.GradeDto;
import com.edu.entity.EduGrade;
import com.edu.vo.GradeVO;

import java.util.List;
import java.util.Map;

public interface GradeService {
    List<EduGrade> myGrades();
    List<EduGrade> courseGrades(Long courseId);
    List<GradeVO> getCourseStudentGrades(Long courseId);
    void saveGrade(GradeDto dto);
    void updateGrade(Long id, GradeDto dto);
    void batchSaveGrades(List<GradeDto> list);
    Map<String, Object> statistics(Long studentId);
    byte[] exportGrades(Long courseId);
}
