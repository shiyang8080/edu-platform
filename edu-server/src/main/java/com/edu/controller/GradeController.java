package com.edu.controller;

import com.edu.common.Result;
import com.edu.dto.GradeDto;
import com.edu.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<?> myGrades() {
        return Result.ok(gradeService.myGrades());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> courseGrades(@PathVariable Long courseId) {
        return Result.ok(gradeService.courseGrades(courseId));
    }

    /** 获取课程选课学生及其成绩（核心接口 — 成绩录入页面使用） */
    @GetMapping("/course/{courseId}/students")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> courseStudentGrades(@PathVariable Long courseId) {
        return Result.ok(gradeService.getCourseStudentGrades(courseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> save(@Valid @RequestBody GradeDto dto) {
        gradeService.saveGrade(dto);
        return Result.ok("录入成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody GradeDto dto) {
        gradeService.updateGrade(id, dto);
        return Result.ok("修改成功");
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public Result<?> batchSave(@RequestBody List<GradeDto> list) {
        gradeService.batchSaveGrades(list);
        return Result.ok("批量录入成功");
    }

    @GetMapping("/statistics/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public Result<?> statistics(@PathVariable Long studentId) {
        return Result.ok(gradeService.statistics(studentId));
    }

    @GetMapping("/export/{courseId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<byte[]> export(@PathVariable Long courseId) throws Exception {
        byte[] data = gradeService.exportGrades(courseId);
        String filename = URLEncoder.encode("成绩表.xlsx", "UTF-8");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
