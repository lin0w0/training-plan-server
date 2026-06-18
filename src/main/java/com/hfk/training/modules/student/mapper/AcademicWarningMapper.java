package com.hfk.training.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.student.entity.AcademicWarning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface AcademicWarningMapper extends BaseMapper<AcademicWarning> {

    @Select("""
        <script>
        SELECT w.*, s.student_no, s.real_name AS student_name, cl.class_name
        FROM academic_warning w
        LEFT JOIN student s ON w.student_id = s.id
        LEFT JOIN class_info cl ON s.class_id = cl.id
        WHERE w.deleted = 0
        <if test='warningLevel != null and warningLevel != \"\"'> AND w.warning_level = #{warningLevel}</if>
        <if test='isResolved != null'> AND w.is_resolved = #{isResolved}</if>
        <if test='studentId != null'> AND w.student_id = #{studentId}</if>
        ORDER BY w.create_time DESC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("warningLevel") String warningLevel,
            @Param("isResolved") Integer isResolved,
            @Param("studentId") Long studentId);
}
