package com.edu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.common.PageResult;
import com.edu.dto.CourseQueryDto;
import com.edu.entity.EduCourse;
import com.edu.entity.SysUser;
import com.edu.mapper.EduCourseMapper;
import com.edu.mapper.SysUserMapper;
import com.edu.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements CourseService {

    private final EduCourseMapper courseMapper;
    private final SysUserMapper userMapper;

    @Override
    public PageResult<EduCourse> pageCourses(CourseQueryDto query) {
        Page<EduCourse> page = new Page<>(query.getPage(), query.getSize());
        page = courseMapper.selectPageWithDetails(page, query.getName(), query.getTeacherId(),
                query.getDepartmentId(), query.getSemester(), query.getStatus());
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public PageResult<EduCourse> myTeachCourses(CourseQueryDto query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        Page<EduCourse> page = new Page<>(query.getPage(), query.getSize());
        page = courseMapper.selectPageWithDetails(page, query.getName(), userId,
                query.getDepartmentId(), query.getSemester(), query.getStatus());
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    @Override
    public EduCourse getCourseDetail(Long id) {
        EduCourse course = getById(id);
        if (course != null && course.getTeacherId() != null) {
            SysUser teacher = userMapper.selectById(course.getTeacherId());
            if (teacher != null) {
                course.setTeacherName(teacher.getRealName());
            }
        }
        return course;
    }

    @Override
    public boolean addCourse(EduCourse course) {
        course.setEnrolled(0);
        return save(course);
    }

    @Override
    public boolean updateCourse(EduCourse course) {
        return updateById(course);
    }
}
