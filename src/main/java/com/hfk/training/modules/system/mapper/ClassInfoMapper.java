package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.modules.system.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface ClassInfoMapper extends BaseMapper<ClassInfo> {

    @Select("""
        <script>
        SELECT cl.*, m.major_name
        FROM class_info cl
        LEFT JOIN major m ON cl.major_id = m.id
        WHERE cl.deleted = 0
        <if test='className != null and className != \"\"'> AND cl.class_name LIKE CONCAT('%', #{className}, '%')</if>
        <if test='classCode != null and classCode != \"\"'> AND cl.class_code LIKE CONCAT('%', #{classCode}, '%')</if>
        <if test='majorId != null'> AND cl.major_id = #{majorId}</if>
        <if test='grade != null'> AND cl.grade = #{grade}</if>
        ORDER BY cl.grade DESC
        </script>
    """)
    Page<Map<String, Object>> pageWithNames(Page<?> page,
            @Param("classCode") String classCode,
            @Param("className") String className,
            @Param("majorId") Long majorId,
            @Param("grade") Integer grade);
}
