package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.system.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Select("""
        <script>
        SELECT t.*, c.college_name
        FROM teacher t
        LEFT JOIN college c ON t.college_id = c.id
        WHERE t.deleted = 0
        <if test='realName != null and realName != \"\"'> AND t.real_name LIKE CONCAT('%', #{realName}, '%')</if>
        <if test='collegeId != null'> AND t.college_id = #{collegeId}</if>
        ORDER BY t.teacher_no ASC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("realName") String realName,
            @Param("collegeId") Long collegeId);
}
