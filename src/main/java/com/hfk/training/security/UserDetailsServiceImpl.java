package com.hfk.training.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.entity.SysRole;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import com.hfk.training.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security UserDetailsService 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getStatus, 1)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 查询用户角色
        List<SysRole> roles = sysRoleMapper.findRolesByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());

        // 查询用户权限
        List<String> permissions = sysRoleMapper.findPermissionsByUserId(user.getId());
        List<SimpleGrantedAuthority> permAuthorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        authorities.addAll(permAuthorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true, true, true,
                authorities
        );
    }
}
