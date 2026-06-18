package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("college")
public class College extends BaseEntity {
    private String collegeCode;
    private String collegeName;
    private String dean;
    private String phone;
    private String email;
    private String website;
    private Integer sortOrder;
    private Integer status;
}
