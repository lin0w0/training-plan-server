package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher")
public class Teacher extends BaseEntity {
    private Long userId;
    private String teacherNo;
    private String realName;
    private Integer gender;
    private String title;
    private Long collegeId;
    private String majorDirection;
    private String phone;
    private String email;
    private Integer status;
}
