package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller
 */
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户增删改查、密码重置")
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    @PreAuthorize("hasAuthority('system:user') or hasRole('ROLE_ADMIN')")
    public Result<PageResult<SysUser>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) Integer status) {
        Page<SysUser> result = sysUserService.pageUsers(page, pageSize, username, realName, userType, status);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setRoles(sysUserService.getUserRoles(id));
        }
        return Result.success(user);
    }

    @PostMapping
    @Operation(summary = "新增用户")
    @PreAuthorize("hasAuthority('system:user') or hasRole('ROLE_ADMIN')")
    public Result<Void> create(@RequestBody SysUser user) {
        sysUserService.save(user);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    @PreAuthorize("hasAuthority('system:user') or hasRole('ROLE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateById(user);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('system:user') or hasRole('ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/reset-pwd")
    @Operation(summary = "重置密码")
    @PreAuthorize("hasAuthority('system:user') or hasRole('ROLE_ADMIN')")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.ok("密码已重置为 123456");
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有用户(下拉选择)")
    public Result<List<SysUser>> all() {
        return Result.success(sysUserService.list());
    }
}
