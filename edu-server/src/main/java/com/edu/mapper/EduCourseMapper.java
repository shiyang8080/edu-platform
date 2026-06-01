package com.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.entity.EduCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EduCourseMapper extends BaseMapper<EduCourse> {

    @Select("<script>" +
        "SELECT c.*, u.real_name AS teacher_name, d.name AS department_name " +
        "FROM edu_course c " +
        "LEFT JOIN sys_user u ON c.teacher_id = u.id " +
        "LEFT JOIN sys_department d ON c.department_id = d.id " +
        "<where>" +
        "  <if test='name != null and name != \"\"'>AND c.name LIKE CONCAT('%', #{name}, '%')</if>" +
        "  <if test='teacherId != null'>AND c.teacher_id = #{teacherId}</if>" +
        "  <if test='departmentId != null'>AND c.department_id = #{departmentId}</if>" +
        "  <if test='semester != null and semester != \"\"'>AND c.semester = #{semester}</if>" +
        "  <if test='status != null'>AND c.status = #{status}</if>" +
        "</where>" +
        "ORDER BY c.create_time DESC" +
        "</script>")
    Page<EduCourse> selectPageWithDetails(Page<EduCourse> page,
                                          @Param("name") String name,
                                          @Param("teacherId") Long teacherId,
                                          @Param("departmentId") Long departmentId,
                                          @Param("semester") String semester,
                                          @Param("status") Integer status);
}
