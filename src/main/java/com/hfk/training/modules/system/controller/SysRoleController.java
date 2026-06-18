package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysRole;
import com.hfk.training.modules.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 */
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色增删改查、权限分配")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询角色")
    @PreAuthorize("hasAuthority('system:role') or hasRole('ROLE_ADMIN')")
    public Result<PageResult<SysRole>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName) {
        Page<SysRole> result = sysRoleService.pageRoles(page, pageSize, roleName);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有角色(下拉选择)")
    public Result<List<SysRole>> all() {
        return Result.success(sysRoleService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情")
    public Result<SysRole> getById(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role != null) {
            role.setPermissionIds(sysRoleService.getPermissionIds(id));
        }
        return Result.success(role);
    }

    @PostMapping
    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('system:role') or hasRole('ROLE_ADMIN')")
    public Result<Void> create(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    @PreAuthorize("hasAuthority('system:role') or hasRole('ROLE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleService.updateById(role);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('system:role') or hasRole('ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/assign-permissions")
    @Operation(summary = "分配权限")
    @PreAuthorize("hasAuthority('system:role') or hasRole('ROLE_ADMIN')")
    public Result<Void> assignPermissions(@RequestBody AssignPermRequest request) {
        sysRoleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        return Result.ok("权限分配成功");
    }

    @Data
    public static class AssignPermRequest {
        private Long roleId;
        private List<Long> permissionIds;
    }
}
