package com.hfk.training.modules.plan.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("training_plan")
public class TrainingPlan extends BaseEntity {
    private String planCode;
    private String planName;
    private Long majorId;
    private Integer enrollmentYearStart;
    private Integer enrollmentYearEnd;
    private Integer duration;
    private Integer totalCredits;
    private Integer requiredCredits;
    private Integer electiveCredits;
    private Integer generalCredits;
    private Integer coreCredits;
    private String status;
    private String description;
}
