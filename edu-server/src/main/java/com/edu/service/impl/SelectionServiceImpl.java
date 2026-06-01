package com.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.entity.EduCourse;
import com.edu.entity.EduSelection;
import com.edu.mapper.EduCourseMapper;
import com.edu.mapper.EduSelectionMapper;
import com.edu.service.SelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelectionServiceImpl extends ServiceImpl<EduSelectionMapper, EduSelection> implements SelectionService {

    private final EduCourseMapper courseMapper;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    @Override
    public List<EduCourse> mySelections() {
        Long studentId = getCurrentUserId();
        List<EduSelection> selections = list(new LambdaQueryWrapper<EduSelection>()
                .eq(EduSelection::getStudentId, studentId)
                .eq(EduSelection::getStatus, 1));
        return selections.stream().map(s -> {
            EduCourse course = courseMapper.selectById(s.getCourseId());
            if (course != null) {
                course.setEnrolled(null);
            }
            return course;
        }).filter(c -> c != null).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void selectCourse(Long courseId) {
        Long studentId = getCurrentUserId();
        EduCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在");
        }
        if (course.getStatus() != 1) {
            throw new IllegalArgumentException("该课程当前不可选");
        }
        if (course.getEnrolled() >= course.getCapacity()) {
            throw new IllegalArgumentException("课程容量已满");
        }

        EduSelection exist = getOne(new LambdaQueryWrapper<EduSelection>()
                .eq(EduSelection::getStudentId, studentId)
                .eq(EduSelection::getCourseId, courseId));
        if (exist != null && exist.getStatus() == 1) {
            throw new IllegalArgumentException("您已选择该课程");
        }

        if (exist != null) {
            exist.setStatus(1);
            updateById(exist);
        } else {
            EduSelection selection = new EduSelection();
            selection.setStudentId(studentId);
            selection.setCourseId(courseId);
            selection.setStatus(1);
            save(selection);
        }

        course.setEnrolled(course.getEnrolled() + 1);
        courseMapper.updateById(course);
    }

    @Override
    @Transactional
    public void dropCourse(Long courseId) {
        Long studentId = getCurrentUserId();
        EduSelection selection = getOne(new LambdaQueryWrapper<EduSelection>()
                .eq(EduSelection::getStudentId, studentId)
                .eq(EduSelection::getCourseId, courseId)
                .eq(EduSelection::getStatus, 1));
        if (selection == null) {
            throw new IllegalArgumentException("您未选择该课程");
        }
        selection.setStatus(0);
        updateById(selection);

        EduCourse course = courseMapper.selectById(courseId);
        if (course != null && course.getEnrolled() > 0) {
            course.setEnrolled(course.getEnrolled() - 1);
            courseMapper.updateById(course);
        }
    }

    @Override
    public List<EduSelection> getCourseStudents(Long courseId) {
        return list(new LambdaQueryWrapper<EduSelection>()
                .eq(EduSelection::getCourseId, courseId)
                .eq(EduSelection::getStatus, 1));
    }
}
