-- ===================================================
-- 综合教学管理平台 - 数据库初始化脚本
-- ===================================================

CREATE DATABASE IF NOT EXISTS `edu_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `edu_platform`;

-- 1. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `real_name` VARCHAR(50),
    `role` ENUM('ADMIN','TEACHER','STUDENT') NOT NULL DEFAULT 'STUDENT',
    `gender` TINYINT DEFAULT 0 COMMENT '0未知 1男 2女',
    `phone` VARCHAR(20),
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `status` TINYINT DEFAULT 1 COMMENT '0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 学院/系部表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系部门表';

-- 3. 课程表
DROP TABLE IF EXISTS `edu_course`;
CREATE TABLE `edu_course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `code` VARCHAR(50) UNIQUE COMMENT '课程编号',
    `teacher_id` BIGINT COMMENT '授课教师',
    `department_id` BIGINT COMMENT '开课院系',
    `credit` DECIMAL(3,1) DEFAULT 0 COMMENT '学分',
    `semester` VARCHAR(20) COMMENT '学期 如2025-2026-1',
    `class_hours` INT DEFAULT 0 COMMENT '总课时',
    `capacity` INT DEFAULT 0 COMMENT '课程容量上限',
    `enrolled` INT DEFAULT 0 COMMENT '已选人数',
    `classroom` VARCHAR(100) COMMENT '上课地点',
    `schedule` VARCHAR(255) COMMENT '上课时间 如"周一1-2节"',
    `description` TEXT COMMENT '课程简介',
    `status` TINYINT DEFAULT 1 COMMENT '0停开 1开设中 2已结课',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 4. 选课记录表
DROP TABLE IF EXISTS `edu_selection`;
CREATE TABLE `edu_selection` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `status` TINYINT DEFAULT 1 COMMENT '0退课 1在修 2已修完',
    `select_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- 5. 成绩表
DROP TABLE IF EXISTS `edu_grade`;
CREATE TABLE `edu_grade` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `regular_score` DECIMAL(5,2) COMMENT '平时成绩',
    `midterm_score` DECIMAL(5,2) COMMENT '期中成绩',
    `final_score` DECIMAL(5,2) COMMENT '期末成绩',
    `total_score` DECIMAL(5,2) COMMENT '总评成绩',
    `grade_point` DECIMAL(3,1) COMMENT '绩点',
    `grade_level` VARCHAR(5) COMMENT '等级 优秀/良好/中等/及格/不及格',
    `remark` VARCHAR(255),
    `record_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_student_course` (`student_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 6. 考勤表
DROP TABLE IF EXISTS `edu_attendance`;
CREATE TABLE `edu_attendance` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `student_id` BIGINT NOT NULL,
    `course_id` BIGINT NOT NULL,
    `attendance_date` DATE NOT NULL,
    `status` ENUM('PRESENT','LATE','LEAVE','ABSENT') NOT NULL DEFAULT 'PRESENT',
    `remark` VARCHAR(255),
    `record_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- 7. 通知公告表
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT,
    `publisher_id` BIGINT,
    `notice_type` ENUM('SYSTEM','COURSE','GRADE','GENERAL') DEFAULT 'GENERAL',
    `priority` TINYINT DEFAULT 0 COMMENT '0普通 1重要 2紧急',
    `target_role` VARCHAR(50) DEFAULT 'ALL' COMMENT '接收角色 ALL/STUDENT/TEACHER',
    `status` TINYINT DEFAULT 1 COMMENT '0草稿 1已发布 2已撤回',
    `publish_time` DATETIME,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知公告表';

-- 8. 通知阅读记录表
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `notice_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知阅读记录表';

-- ===================================================
-- 初始化数据
-- ===================================================

-- 默认管理员（密码: admin123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `gender`, `phone`, `email`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'ADMIN', 1, '13800000000', 'admin@edu.com', 1);

-- 示例教师
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `gender`, `phone`, `email`, `status`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '张教授', 'TEACHER', 1, '13800000001', 'zhang@edu.com', 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '李教授', 'TEACHER', 2, '13800000002', 'li@edu.com', 1),
('teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '王讲师', 'TEACHER', 1, '13800000003', 'wang@edu.com', 1);

-- 示例学生
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `gender`, `phone`, `email`, `status`) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '赵同学', 'STUDENT', 1, '13900000001', 'zhao@edu.com', 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '钱同学', 'STUDENT', 2, '13900000002', 'qian@edu.com', 1),
('student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '孙同学', 'STUDENT', 1, '13900000003', 'sun@edu.com', 1),
('student4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '周同学', 'STUDENT', 2, '13900000004', 'zhou@edu.com', 1);

-- 示例院系
INSERT INTO `sys_department` (`name`, `description`) VALUES
('计算机科学与技术学院', '计算机相关专业'),
('数学与统计学院', '数学、统计学相关专业'),
('外国语学院', '外语相关专业'),
('经济管理学院', '经管相关专业');

-- 示例课程
INSERT INTO `edu_course` (`name`, `code`, `teacher_id`, `department_id`, `credit`, `semester`, `class_hours`, `capacity`, `enrolled`, `classroom`, `schedule`, `description`, `status`) VALUES
('Java程序设计', 'CS001', 2, 1, 4.0, '2025-2026-1', 64, 60, 2, '教一楼301', '周一1-2节,周三3-4节', '面向对象程序设计基础，涵盖Java核心语法、集合框架、IO操作等', 1),
('数据结构与算法', 'CS002', 2, 1, 3.5, '2025-2026-1', 48, 50, 0, '教一楼201', '周二1-2节,周四3-4节', '常用数据结构和算法分析与设计', 1),
('数据库原理', 'CS003', 3, 1, 3.0, '2025-2026-1', 48, 40, 0, '教二楼101', '周一5-6节,周三1-2节', '关系数据库原理与SQL', 1),
('高等数学A', 'MATH001', 4, 2, 5.0, '2025-2026-1', 80, 100, 0, '主楼301', '周二3-4节,周四1-2节,周五5-6节', '微积分与线性代数', 1),
('大学英语', 'ENG001', 2, 3, 3.0, '2025-2026-1', 48, 80, 0, '外语楼201', '周三5-6节,周五1-2节', '综合英语听说读写训练', 1),
('Python数据分析', 'CS004', 3, 1, 3.0, '2025-2026-1', 48, 40, 0, '教一楼401', '周二5-6节,周四5-6节', 'Python编程与数据分析实战', 1);

-- 示例选课（学生1选了Java程序设计）
INSERT INTO `edu_selection` (`student_id`, `course_id`, `status`) VALUES (5, 1, 1);
-- 学生2选了Java程序设计
INSERT INTO `edu_selection` (`student_id`, `course_id`, `status`) VALUES (6, 1, 1);

-- 示例成绩
INSERT INTO `edu_grade` (`student_id`, `course_id`, `regular_score`, `midterm_score`, `final_score`, `total_score`, `grade_point`, `grade_level`) VALUES
(5, 1, 85.00, 88.00, 90.00, 88.00, 3.8, '良好'),
(6, 1, 78.00, 82.00, 85.00, 82.00, 3.2, '良好');

-- 示例考勤
INSERT INTO `edu_attendance` (`student_id`, `course_id`, `attendance_date`, `status`) VALUES
(5, 1, '2025-09-01', 'PRESENT'),
(5, 1, '2025-09-03', 'PRESENT'),
(5, 1, '2025-09-08', 'LATE'),
(6, 1, '2025-09-01', 'PRESENT'),
(6, 1, '2025-09-03', 'ABSENT');

-- 示例通知
INSERT INTO `sys_notice` (`title`, `content`, `publisher_id`, `notice_type`, `priority`, `target_role`, `publish_time`, `status`) VALUES
('选课系统开放通知', '本学期选课系统将于2025年9月1日正式开放，请各位同学在规定时间内完成选课操作。', 1, 'SYSTEM', 1, 'STUDENT', '2025-08-28 09:00:00', 1),
('期中教学检查通知', '学院将于第10周进行期中教学检查，请各位教师提前做好准备。', 1, 'COURSE', 1, 'TEACHER', '2025-10-15 14:00:00', 1),
('关于规范课堂考勤的通知', '为进一步加强学风建设，请各位教师严格按照规定进行课堂考勤记录。', 1, 'GENERAL', 0, 'TEACHER', '2025-09-05 10:00:00', 1);

-- 注意: 密码使用BCrypt加密，示例密码均为 "123456"
-- 生产环境请使用 BCryptPasswordEncoder 生成实际密码
