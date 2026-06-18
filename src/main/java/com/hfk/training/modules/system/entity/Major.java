package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("major")
public class Major extends BaseEntity {
    private String majorCode;
    private String majorName;
    private Long collegeId;
    private String disciplineCategory;
    private String degreeType;
    private Integer duration;
    private String level;
    private Integer totalCredits;
    private String trainingObjective;
    private String graduationRequirements;
    private Integer status;
}
