package com.hfk.training.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.course.entity.CoursePrerequisite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CoursePrerequisiteMapper extends BaseMapper<CoursePrerequisite> {

    /**
     * 查询课程的先修课程列表 (含课程名称)
     */
    @Select("""
        SELECT cp.id, cp.course_id, cp.prerequisite_id, cp.is_strict,
               c.course_code, c.course_name, c.credit
        FROM course_prerequisite cp
        INNER JOIN course c ON cp.prerequisite_id = c.id
        WHERE cp.course_id = #{courseId} AND cp.deleted = 0
        ORDER BY c.course_code
    """)
    List<Map<String, Object>> findPrerequisitesByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询某课程是哪些课程的先修课
     */
    @Select("""
        SELECT cp.id, cp.course_id, cp.prerequisite_id, c.course_code, c.course_name
        FROM course_prerequisite cp
        INNER JOIN course c ON cp.course_id = c.id
        WHERE cp.prerequisite_id = #{courseId} AND cp.deleted = 0
    """)
    List<Map<String, Object>> findDependentsByCourseId(@Param("courseId") Long courseId);
}
