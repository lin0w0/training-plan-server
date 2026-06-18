package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体 (表名 class 是 MySQL 保留字，用 ClassInfo 避免 Java 关键字冲突)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class_info")
public class ClassInfo extends BaseEntity {
    private String classCode;
    private String className;
    private Long majorId;
    private Integer grade;
    private String headTeacher;
    private Integer studentCount;
    private Integer status;
}
