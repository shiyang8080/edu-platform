package com.edu.controller;

import com.edu.common.Result;
import com.edu.dto.CourseQueryDto;
import com.edu.entity.EduCourse;
import com.edu.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public Result<?> list(CourseQueryDto query) {
        return Result.ok(courseService.pageCourses(query));
    }

    @GetMapping("/my-teach")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> myTeach(CourseQueryDto query) {
        return Result.ok(courseService.myTeachCourses(query));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(courseService.getCourseDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<?> create(@RequestBody EduCourse course) {
        courseService.addCourse(course);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<?> update(@PathVariable Long id, @RequestBody EduCourse course) {
        course.setId(id);
        courseService.updateCourse(course);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.ok("删除成功");
    }
}
