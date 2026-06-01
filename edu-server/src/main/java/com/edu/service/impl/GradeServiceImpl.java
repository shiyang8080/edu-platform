package com.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.dto.GradeDto;
import com.edu.entity.EduGrade;
import com.edu.entity.EduSelection;
import com.edu.entity.SysUser;
import com.edu.mapper.EduGradeMapper;
import com.edu.mapper.EduSelectionMapper;
import com.edu.mapper.SysUserMapper;
import com.edu.service.GradeService;
import com.edu.vo.GradeVO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl extends ServiceImpl<EduGradeMapper, EduGrade> implements GradeService {

    private final EduGradeMapper gradeMapper;
    private final SysUserMapper userMapper;
    private final EduSelectionMapper selectionMapper;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    // ========== 核心新方法：获取课程选课学生 + 已有成绩合并 ==========
    @Override
    public List<GradeVO> getCourseStudentGrades(Long courseId) {
        // 1. 获取该课程所有在修学生
        List<EduSelection> selections = selectionMapper.selectList(
                new LambdaQueryWrapper<EduSelection>()
                        .eq(EduSelection::getCourseId, courseId)
                        .eq(EduSelection::getStatus, 1));

        // 2. 获取该课程已有成绩记录
        List<EduGrade> existingGrades = list(new LambdaQueryWrapper<EduGrade>()
                .eq(EduGrade::getCourseId, courseId));
        Map<Long, EduGrade> gradeMap = existingGrades.stream()
                .collect(Collectors.toMap(EduGrade::getStudentId, g -> g, (a, b) -> a));

        // 3. 批量获取学生姓名
        Set<Long> studentIds = selections.stream().map(EduSelection::getStudentId).collect(Collectors.toSet());
        Map<Long, SysUser> userMap = new HashMap<>();
        if (!studentIds.isEmpty()) {
            List<SysUser> users = userMapper.selectBatchIds(studentIds);
            users.forEach(u -> userMap.put(u.getId(), u));
        }

        // 4. 组装 GradeVO
        List<GradeVO> result = new ArrayList<>();
        for (EduSelection sel : selections) {
            GradeVO vo = new GradeVO();
            vo.setSelectionId(sel.getId());
            vo.setStudentId(sel.getStudentId());
            vo.setCourseId(sel.getCourseId());

            SysUser student = userMap.get(sel.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getRealName());
                vo.setUsername(student.getUsername());
            }

            EduGrade grade = gradeMap.get(sel.getStudentId());
            if (grade != null) {
                vo.setGradeId(grade.getId());
                vo.setRegularScore(grade.getRegularScore());
                vo.setMidtermScore(grade.getMidtermScore());
                vo.setFinalScore(grade.getFinalScore());
                vo.setTotalScore(grade.getTotalScore());
                vo.setGradePoint(grade.getGradePoint());
                vo.setGradeLevel(grade.getGradeLevel());
                vo.setRemark(grade.getRemark());
                vo.setRecordTime(grade.getRecordTime());
                vo.setUpdateTime(grade.getUpdateTime());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<EduGrade> myGrades() {
        Long studentId = getCurrentUserId();
        List<EduGrade> grades = list(new LambdaQueryWrapper<EduGrade>()
                .eq(EduGrade::getStudentId, studentId));
        // populate course names
        for (EduGrade g : grades) {
            SysUser teacher = userMapper.selectById(g.getStudentId());
            if (teacher != null) {
                g.setStudentName(teacher.getRealName());
            }
        }
        return grades;
    }

    @Override
    public List<EduGrade> courseGrades(Long courseId) {
        List<EduGrade> grades = list(new LambdaQueryWrapper<EduGrade>()
                .eq(EduGrade::getCourseId, courseId));
        // populate student names
        for (EduGrade g : grades) {
            SysUser student = userMapper.selectById(g.getStudentId());
            if (student != null) {
                g.setStudentName(student.getRealName());
            }
        }
        return grades;
    }

    @Override
    @Transactional
    public void saveGrade(GradeDto dto) {
        // 自动 upsert：存在则更新，不存在则新增
        EduGrade exist = getOne(new LambdaQueryWrapper<EduGrade>()
                .eq(EduGrade::getStudentId, dto.getStudentId())
                .eq(EduGrade::getCourseId, dto.getCourseId()));
        if (exist != null) {
            // 已存在，走更新逻辑
            exist.setRegularScore(dto.getRegularScore());
            exist.setMidtermScore(dto.getMidtermScore());
            exist.setFinalScore(dto.getFinalScore());
            calculateTotal(exist);
            exist.setRemark(dto.getRemark());
            updateById(exist);
        } else {
            EduGrade grade = new EduGrade();
            grade.setStudentId(dto.getStudentId());
            grade.setCourseId(dto.getCourseId());
            grade.setRegularScore(dto.getRegularScore());
            grade.setMidtermScore(dto.getMidtermScore());
            grade.setFinalScore(dto.getFinalScore());
            calculateTotal(grade);
            grade.setRemark(dto.getRemark());
            save(grade);
        }
    }

    @Override
    public void updateGrade(Long id, GradeDto dto) {
        EduGrade grade = getById(id);
        if (grade == null) {
            throw new IllegalArgumentException("成绩记录不存在");
        }
        grade.setRegularScore(dto.getRegularScore());
        grade.setMidtermScore(dto.getMidtermScore());
        grade.setFinalScore(dto.getFinalScore());
        calculateTotal(grade);
        grade.setRemark(dto.getRemark());
        updateById(grade);
    }

    @Override
    @Transactional
    public void batchSaveGrades(List<GradeDto> list) {
        for (GradeDto dto : list) {
            if (dto.getStudentId() == null || dto.getCourseId() == null) continue;
            saveGrade(dto);  // upsert
        }
    }

    private void calculateTotal(EduGrade grade) {
        BigDecimal regular = grade.getRegularScore() != null ? grade.getRegularScore() : BigDecimal.ZERO;
        BigDecimal midterm = grade.getMidtermScore() != null ? grade.getMidtermScore() : BigDecimal.ZERO;
        BigDecimal finals = grade.getFinalScore() != null ? grade.getFinalScore() : BigDecimal.ZERO;

        BigDecimal total = regular.multiply(new BigDecimal("0.3"))
                .add(midterm.multiply(new BigDecimal("0.3")))
                .add(finals.multiply(new BigDecimal("0.4")))
                .setScale(2, RoundingMode.HALF_UP);
        grade.setTotalScore(total);

        double gpa = Math.max(0, Math.min(4.0, (total.doubleValue() - 50) / 10.0));
        grade.setGradePoint(BigDecimal.valueOf(gpa).setScale(1, RoundingMode.HALF_UP));

        if (total.doubleValue() >= 90) grade.setGradeLevel("优秀");
        else if (total.doubleValue() >= 80) grade.setGradeLevel("良好");
        else if (total.doubleValue() >= 70) grade.setGradeLevel("中等");
        else if (total.doubleValue() >= 60) grade.setGradeLevel("及格");
        else grade.setGradeLevel("不及格");
    }

    @Override
    public Map<String, Object> statistics(Long studentId) {
        List<EduGrade> grades = list(new LambdaQueryWrapper<EduGrade>()
                .eq(EduGrade::getStudentId, studentId));
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCourses", grades.size());

        double sum = grades.stream().mapToDouble(g -> g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0).sum();
        stats.put("averageScore", grades.isEmpty() ? 0 : BigDecimal.valueOf(sum / grades.size()).setScale(2, RoundingMode.HALF_UP));

        double gpaSum = grades.stream().mapToDouble(g -> g.getGradePoint() != null ? g.getGradePoint().doubleValue() : 0).sum();
        stats.put("averageGpa", grades.isEmpty() ? 0 : BigDecimal.valueOf(gpaSum / grades.size()).setScale(2, RoundingMode.HALF_UP));

        long passed = grades.stream().filter(g -> g.getTotalScore() != null && g.getTotalScore().doubleValue() >= 60).count();
        stats.put("passedCourses", passed);
        stats.put("failedCourses", grades.size() - passed);
        stats.put("grades", grades);

        return stats;
    }

    @Override
    public byte[] exportGrades(Long courseId) {
        List<EduGrade> grades = courseGrades(courseId);
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("成绩表");
            Row header = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "平时成绩", "期中成绩", "期末成绩", "总评成绩", "绩点", "等级"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
            for (int i = 0; i < grades.size(); i++) {
                EduGrade g = grades.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(g.getStudentId() != null ? g.getStudentId().toString() : "");
                SysUser student = userMapper.selectById(g.getStudentId());
                row.createCell(1).setCellValue(student != null ? student.getRealName() : "");
                row.createCell(2).setCellValue(g.getRegularScore() != null ? g.getRegularScore().doubleValue() : 0);
                row.createCell(3).setCellValue(g.getMidtermScore() != null ? g.getMidtermScore().doubleValue() : 0);
                row.createCell(4).setCellValue(g.getFinalScore() != null ? g.getFinalScore().doubleValue() : 0);
                row.createCell(5).setCellValue(g.getTotalScore() != null ? g.getTotalScore().doubleValue() : 0);
                row.createCell(6).setCellValue(g.getGradePoint() != null ? g.getGradePoint().doubleValue() : 0);
                row.createCell(7).setCellValue(g.getGradeLevel() != null ? g.getGradeLevel() : "");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }
}
