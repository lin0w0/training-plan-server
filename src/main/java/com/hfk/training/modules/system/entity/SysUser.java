package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    /** 用户名(学号/工号) */
    private String username;

    /** 密码(BCrypt加密) */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 性别: 0-未知 1-男 2-女 */
    private Integer gender;

    /** 用户类型: admin/teacher/student */
    private String userType;

    /** 状态: 0-禁用 1-启用 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 所属学院ID */
    private Long collegeId;

    /** 角色列表 (非表字段) */
    @TableField(exist = false)
    private List<SysRole> roles;

    /** 角色ID列表 (非表字段) */
    @TableField(exist = false)
    private List<Long> roleIds;
}
