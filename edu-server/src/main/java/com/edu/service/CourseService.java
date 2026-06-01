package com.edu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.common.PageResult;
import com.edu.dto.CourseQueryDto;
import com.edu.entity.EduCourse;

public interface CourseService extends IService<EduCourse> {
    PageResult<EduCourse> pageCourses(CourseQueryDto query);
    PageResult<EduCourse> myTeachCourses(CourseQueryDto query);
    EduCourse getCourseDetail(Long id);
    boolean addCourse(EduCourse course);
    boolean updateCourse(EduCourse course);
}
