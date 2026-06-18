package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.system.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生（含学院/专业/班级名称）
     */
    @Select("""
        <script>
        SELECT s.*, c.college_name, m.major_name, cl.class_name
        FROM student s
        LEFT JOIN college c ON s.college_id = c.id
        LEFT JOIN major m ON s.major_id = m.id
        LEFT JOIN class_info cl ON s.class_id = cl.id
        WHERE s.deleted = 0
        <if test='studentNo != null and studentNo != \"\"'> AND s.student_no LIKE CONCAT('%', #{studentNo}, '%')</if>
        <if test='realName != null and realName != \"\"'> AND s.real_name LIKE CONCAT('%', #{realName}, '%')</if>
        <if test='collegeId != null'> AND s.college_id = #{collegeId}</if>
        <if test='majorId != null'> AND s.major_id = #{majorId}</if>
        <if test='classId != null'> AND s.class_id = #{classId}</if>
        <if test='enrollmentYear != null'> AND s.enrollment_year = #{enrollmentYear}</if>
        ORDER BY s.enrollment_year DESC, s.student_no ASC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("studentNo") String studentNo,
            @Param("realName") String realName,
            @Param("collegeId") Long collegeId,
            @Param("majorId") Long majorId,
            @Param("classId") Long classId,
            @Param("enrollmentYear") Integer enrollmentYear);
}
