package com.hfk.training.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.course.entity.TeachingSyllabus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface SyllabusMapper extends BaseMapper<TeachingSyllabus> {

    @Select("""
        <script>
        SELECT s.*, c.course_name, t.real_name AS teacher_name
        FROM teaching_syllabus s
        LEFT JOIN course c ON s.course_id = c.id
        LEFT JOIN teacher t ON s.teacher_id = t.id
        WHERE s.deleted = 0
        <if test='courseId != null'> AND s.course_id = #{courseId}</if>
        <if test='teacherId != null'> AND s.teacher_id = #{teacherId}</if>
        ORDER BY s.create_time DESC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("courseId") Long courseId,
            @Param("teacherId") Long teacherId);
}
