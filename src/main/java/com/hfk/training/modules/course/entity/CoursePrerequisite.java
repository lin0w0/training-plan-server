package com.hfk.training.modules.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("course_prerequisite")
public class CoursePrerequisite extends BaseEntity {
    /** 课程ID */
    private Long courseId;
    /** 先修课程ID */
    private Long prerequisiteId;
    /** 是否强制: 0-建议 1-强制 */
    private Integer isStrict;
}
