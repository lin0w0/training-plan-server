package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.system.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {

    @Select("""
        <script>
        SELECT m.*, c.college_name
        FROM major m
        LEFT JOIN college c ON m.college_id = c.id
        WHERE m.deleted = 0
        <if test='majorCode != null and majorCode != \"\"'> AND m.major_code LIKE CONCAT('%', #{majorCode}, '%')</if>
        <if test='majorName != null and majorName != \"\"'> AND m.major_name LIKE CONCAT('%', #{majorName}, '%')</if>
        <if test='collegeId != null'> AND m.college_id = #{collegeId}</if>
        ORDER BY m.major_code ASC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("majorCode") String majorCode,
            @Param("majorName") String majorName,
            @Param("collegeId") Long collegeId);
}
