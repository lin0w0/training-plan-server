package com.hfk.training.modules.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
