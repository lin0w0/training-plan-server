package com.hfk.training.modules.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hfk.training.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class SysPermission extends BaseEntity {

    /** 父权限ID (0表示顶级) */
    private Long parentId;

    /** 权限名称 */
    private String permName;

    /** 权限编码 (如 system:user:list) */
    private String permCode;

    /** 权限类型: menu/button/api */
    private String permType;

    /** 路由路径 */
    private String path;

    /** 前端组件路径 */
    private String component;

    /** 图标 */
    private String icon;

    /** 排序 */
    private Integer sortOrder;

    /** 状态 */
    private Integer status;

    /** 子权限 (非表字段) */
    @TableField(exist = false)
    private List<SysPermission> children;
}
