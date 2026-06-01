package com.edu.service;

import com.edu.entity.EduCourse;
import com.edu.entity.EduSelection;
import java.util.List;

public interface SelectionService {
    List<EduCourse> mySelections();
    void selectCourse(Long courseId);
    void dropCourse(Long courseId);
    List<EduSelection> getCourseStudents(Long courseId);
}
