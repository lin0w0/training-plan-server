package com.hfk.training.modules.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teaching_syllabus")
public class TeachingSyllabus extends BaseEntity {
    /** 课程名称 (非表字段) */
    @TableField(exist = false)
    private String courseName;
    /** 教师姓名 (非表字段) */
    @TableField(exist = false)
    private String teacherName;
    private Long courseId;
    private Long teacherId;
    private String semester;
    private String teachingObjective;
    private String teachingContent;
    private String textbook;
    private String textbookIsbn;
    private String referenceBooks;
    private String assessmentMethod;
    private String weeklySchedule;
    private String status;
}
