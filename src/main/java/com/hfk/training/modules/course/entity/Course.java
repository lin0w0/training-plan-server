package com.hfk.training.modules.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course")
public class Course extends BaseEntity {
    private String courseCode;
    private String courseName;
    private String enName;
    private BigDecimal credit;
    private Integer classHour;
    private Integer lectureHour;
    private Integer labHour;
    private String courseType;
    private String courseCategory;
    private Long collegeId;
    private Integer semester;
    private String examType;
    private String description;
    private Integer status;
}
