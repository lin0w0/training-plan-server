package com.hfk.training.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.common.BusinessException;
import com.hfk.training.modules.system.entity.SysRole;
import com.hfk.training.modules.system.mapper.SysPermissionMapper;
import com.hfk.training.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色 Service
 */
@Service
@RequiredArgsConstructor
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    private final SysPermissionMapper sysPermissionMapper;
    private final JdbcTemplate jdbcTemplate;

    public Page<SysRole> pageRoles(int page, int pageSize, String roleName) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .orderByAsc(SysRole::getSortOrder);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    @Transactional
    public boolean save(SysRole role) {
        Long count = lambdaQuery().eq(SysRole::getRoleCode, role.getRoleCode()).count();
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }
        return super.save(role);
    }

    /**
     * 分配权限
     */
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有权限
        jdbcTemplate.update("DELETE FROM sys_role_permission WHERE role_id = ?", roleId);
        // 插入新权限
        for (Long permId : permissionIds) {
            jdbcTemplate.update(
                    "INSERT INTO sys_role_permission (role_id, permission_id) VALUES (?, ?)",
                    roleId, permId);
        }
    }

    /**
     * 获取角色的权限ID列表
     */
    public List<Long> getPermissionIds(Long roleId) {
        return sysPermissionMapper.findPermissionIdsByRoleId(roleId);
    }
}
