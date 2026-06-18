package com.hfk.training.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.common.BusinessException;
import com.hfk.training.modules.system.entity.SysRole;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.mapper.SysRoleMapper;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户 Service
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final PasswordEncoder passwordEncoder;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;

    public Page<SysUser> pageUsers(int page, int pageSize, String username, String realName, String userType, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username)
                .like(StringUtils.hasText(realName), SysUser::getRealName, realName)
                .eq(StringUtils.hasText(userType), SysUser::getUserType, userType)
                .eq(status != null, SysUser::getStatus, status)
                .orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    @Transactional
    public boolean save(SysUser user) {
        // 检查用户名唯一
        Long count = lambdaQuery().eq(SysUser::getUsername, user.getUsername()).count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 密码加密
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode("123456"));
        }
        return super.save(user);
    }

    @Override
    @Transactional
    public boolean updateById(SysUser user) {
        // 不更新密码
        user.setPassword(null);
        return super.updateById(user);
    }

    @Transactional
    public void resetPassword(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode("123456"));
        updateById(user);
    }

    /**
     * 获取用户的角色列表
     */
    public List<SysRole> getUserRoles(Long userId) {
        return sysRoleMapper.findRolesByUserId(userId);
    }
}
