package com.hfk.training.modules.student.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("academic_warning")
public class AcademicWarning extends BaseEntity {
    private Long studentId;
    private String warningType;
    private String warningLevel;
    private String warningContent;
    private Integer isResolved;
    private LocalDateTime resolveTime;
    private String resolveRemark;
}
