package com.hfk.training.modules.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("plan_course")
public class PlanCourse extends BaseEntity {
    private Long planId;
    private Long courseId;
    private Integer semester;
    private String courseTypeInPlan;
    private String courseCategoryInPlan;
    private Integer isRequired;
    private BigDecimal suggestCredit;
    private Integer sortOrder;
}
