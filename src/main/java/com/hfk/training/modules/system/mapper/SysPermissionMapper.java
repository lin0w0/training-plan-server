package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.system.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查询角色的所有权限ID
     */
    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询所有启用的权限(树形)
     */
    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND status = 1 ORDER BY sort_order")
    List<SysPermission> findAllEnabled();
}
