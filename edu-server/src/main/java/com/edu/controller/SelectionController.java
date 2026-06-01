package com.edu.controller;

import com.edu.common.Result;
import com.edu.service.SelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/selections")
@RequiredArgsConstructor
public class SelectionController {

    private final SelectionService selectionService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> mySelections() {
        return Result.ok(selectionService.mySelections());
    }

    @PostMapping("/select/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> select(@PathVariable Long courseId) {
        selectionService.selectCourse(courseId);
        return Result.ok("选课成功");
    }

    @DeleteMapping("/drop/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> drop(@PathVariable Long courseId) {
        selectionService.dropCourse(courseId);
        return Result.ok("退课成功");
    }

    @GetMapping("/course/{courseId}/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> courseStudents(@PathVariable Long courseId) {
        return Result.ok(selectionService.getCourseStudents(courseId));
    }
}
