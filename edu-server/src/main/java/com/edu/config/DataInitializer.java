package com.edu.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.edu.entity.*;
import com.edu.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final SysDepartmentMapper departmentMapper;
    private final EduCourseMapper courseMapper;
    private final EduSelectionMapper selectionMapper;
    private final EduGradeMapper gradeMapper;
    private final SysNoticeMapper noticeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (hasData()) {
            log.info("数据库已初始化，跳过");
            return;
        }
        log.info("开始初始化数据...");
        initUsers();
        initDepartments();
        initCourses();
        initSelections();
        initGrades();
        initNotices();
        log.info("数据初始化完成");
    }

    private boolean hasData() {
        return userMapper.selectCount(null) > 0;
    }

    private void initUsers() {
        String pwd = passwordEncoder.encode("123456");
        userMapper.insert(buildUser("admin", pwd, "系统管理员", "ADMIN", 1, "13800000000", "admin@edu.com"));
        userMapper.insert(buildUser("teacher1", pwd, "张教授", "TEACHER", 1, "13800000001", "zhang@edu.com"));
        userMapper.insert(buildUser("teacher2", pwd, "李教授", "TEACHER", 2, "13800000002", "li@edu.com"));
        userMapper.insert(buildUser("teacher3", pwd, "王讲师", "TEACHER", 1, "13800000003", "wang@edu.com"));
        userMapper.insert(buildUser("student1", pwd, "赵同学", "STUDENT", 1, "13900000001", "zhao@edu.com"));
        userMapper.insert(buildUser("student2", pwd, "钱同学", "STUDENT", 2, "13900000002", "qian@edu.com"));
        userMapper.insert(buildUser("student3", pwd, "孙同学", "STUDENT", 1, "13900000003", "sun@edu.com"));
        userMapper.insert(buildUser("student4", pwd, "周同学", "STUDENT", 2, "13900000004", "zhou@edu.com"));
    }

    private SysUser buildUser(String username, String password, String realName, String role, Integer gender, String phone, String email) {
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setRole(role);
        user.setGender(gender);
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(1);
        return user;
    }

    private void initDepartments() {
        departmentMapper.insert(buildDept("计算机科学与技术学院", "计算机相关专业"));
        departmentMapper.insert(buildDept("数学与统计学院", "数学、统计学相关专业"));
        departmentMapper.insert(buildDept("外国语学院", "外语相关专业"));
        departmentMapper.insert(buildDept("经济管理学院", "经管相关专业"));
    }

    private SysDepartment buildDept(String name, String desc) {
        SysDepartment d = new SysDepartment();
        d.setName(name);
        d.setDescription(desc);
        return d;
    }

    private void initCourses() {
        courseMapper.insert(buildCourse("Java程序设计", "CS001", 2L, 1L, 4.0, "2025-2026-1", 64, 60, 2, "教一楼301", "周一1-2节,周三3-4节", "面向对象程序设计基础"));
        courseMapper.insert(buildCourse("数据结构与算法", "CS002", 2L, 1L, 3.5, "2025-2026-1", 48, 50, 0, "教一楼201", "周二1-2节,周四3-4节", "常用数据结构和算法分析与设计"));
        courseMapper.insert(buildCourse("数据库原理", "CS003", 3L, 1L, 3.0, "2025-2026-1", 48, 40, 0, "教二楼101", "周一5-6节,周三1-2节", "关系数据库原理与SQL"));
        courseMapper.insert(buildCourse("高等数学A", "MATH001", 4L, 2L, 5.0, "2025-2026-1", 80, 100, 0, "主楼301", "周二3-4节,周四1-2节,周五5-6节", "微积分与线性代数"));
        courseMapper.insert(buildCourse("大学英语", "ENG001", 2L, 3L, 3.0, "2025-2026-1", 48, 80, 0, "外语楼201", "周三5-6节,周五1-2节", "综合英语听说读写训练"));
        courseMapper.insert(buildCourse("Python数据分析", "CS004", 3L, 1L, 3.0, "2025-2026-1", 48, 40, 0, "教一楼401", "周二5-6节,周四5-6节", "Python编程与数据分析实战"));
    }

    private EduCourse buildCourse(String name, String code, Long teacherId, Long deptId, double credit,
                                   String semester, int hours, int capacity, int enrolled,
                                   String classroom, String schedule, String desc) {
        EduCourse c = new EduCourse();
        c.setName(name);
        c.setCode(code);
        c.setTeacherId(teacherId);
        c.setDepartmentId(deptId);
        c.setCredit(BigDecimal.valueOf(credit));
        c.setSemester(semester);
        c.setClassHours(hours);
        c.setCapacity(capacity);
        c.setEnrolled(enrolled);
        c.setClassroom(classroom);
        c.setSchedule(schedule);
        c.setDescription(desc);
        c.setStatus(1);
        return c;
    }

    private void initSelections() {
        insertSelection(5L, 1L, 1);
        insertSelection(6L, 1L, 1);
    }

    private void insertSelection(Long studentId, Long courseId, Integer status) {
        EduSelection s = new EduSelection();
        s.setStudentId(studentId);
        s.setCourseId(courseId);
        s.setStatus(status);
        selectionMapper.insert(s);
    }

    private void initGrades() {
        insertGrade(5L, 1L, 85, 88, 90, 88.0, 3.8, "良好");
        insertGrade(6L, 1L, 78, 82, 85, 82.0, 3.2, "良好");
    }

    private void insertGrade(Long studentId, Long courseId, double regular, double midterm, double finals,
                              double total, double gpa, String level) {
        EduGrade g = new EduGrade();
        g.setStudentId(studentId);
        g.setCourseId(courseId);
        g.setRegularScore(BigDecimal.valueOf(regular));
        g.setMidtermScore(BigDecimal.valueOf(midterm));
        g.setFinalScore(BigDecimal.valueOf(finals));
        g.setTotalScore(BigDecimal.valueOf(total));
        g.setGradePoint(BigDecimal.valueOf(gpa));
        g.setGradeLevel(level);
        gradeMapper.insert(g);
    }

    private void initNotices() {
        insertNotice("选课系统开放通知", "本学期选课系统将于2025年9月1日正式开放，请各位同学在规定时间内完成选课操作。", 1L, "SYSTEM", 1, "STUDENT");
        insertNotice("期中教学检查通知", "学院将于第10周进行期中教学检查，请各位教师提前做好准备。", 1L, "COURSE", 1, "TEACHER");
        insertNotice("关于规范课堂考勤的通知", "为进一步加强学风建设，请各位教师严格按照规定进行课堂考勤记录。", 1L, "GENERAL", 0, "TEACHER");
    }

    private void insertNotice(String title, String content, Long publisherId, String type, Integer priority, String targetRole) {
        SysNotice n = new SysNotice();
        n.setTitle(title);
        n.setContent(content);
        n.setPublisherId(publisherId);
        n.setNoticeType(type);
        n.setPriority(priority);
        n.setTargetRole(targetRole);
        n.setStatus(1);
        n.setPublishTime(LocalDateTime.now());
        noticeMapper.insert(n);
    }
}
