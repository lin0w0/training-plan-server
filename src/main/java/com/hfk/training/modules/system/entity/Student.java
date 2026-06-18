package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student")
public class Student extends BaseEntity {
    private Long userId;
    private String studentNo;
    private String realName;
    private Integer gender;
    private LocalDate birthDate;
    private String idCard;
    private Integer enrollmentYear;
    private Long classId;
    private Long majorId;
    private Long collegeId;
    private Long trainingPlanId;
    private String status;
    private String phone;
    private String email;
    private String address;
}
