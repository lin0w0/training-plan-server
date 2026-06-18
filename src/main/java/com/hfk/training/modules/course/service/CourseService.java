package com.hfk.training.modules.course.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.modules.course.entity.Course;
import com.hfk.training.modules.course.mapper.CourseMapper;
import org.springframework.stereotype.Service;

@Service
public class CourseService extends ServiceImpl<CourseMapper, Course> {
}
