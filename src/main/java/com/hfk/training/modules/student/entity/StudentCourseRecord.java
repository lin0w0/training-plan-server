package com.hfk.training.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_course_record")
public class StudentCourseRecord extends BaseEntity {
    private Long studentId;
    private Long courseId;
    private String semester;
    private BigDecimal score;
    private BigDecimal gradePoint;
    private Integer isPass;
    private Integer isRetake;
    private String examType;
    private String status;
}
