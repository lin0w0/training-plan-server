package com.hfk.training.modules.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.plan.entity.PlanCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlanCourseMapper extends BaseMapper<PlanCourse> {

    /**
     * 查询版本课程（含课程详情: 名称/学分/性质/分类）
     */
    @Select("""
        SELECT pc.*, c.course_code, c.course_name, c.credit, c.course_type,
               c.course_category, c.class_hour, c.exam_type
        FROM plan_course pc
        INNER JOIN course c ON pc.course_id = c.id
        WHERE pc.plan_id = #{planId} AND pc.deleted = 0
        ORDER BY pc.semester, pc.sort_order
    """)
    List<Map<String, Object>> findCoursesWithDetail(@Param("planId") Long planId);
}

