package com.hfk.training.modules.system.controller;

import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysPermission;
import com.hfk.training.modules.system.mapper.SysPermissionMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/permission")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "权限树查询")
public class SysPermissionController {

    private final SysPermissionMapper permissionMapper;

    @GetMapping("/tree")
    @Operation(summary = "获取完整权限树")
    public Result<List<SysPermission>> tree() {
        List<SysPermission> all = permissionMapper.findAllEnabled();
        // 构建树形结构
        Map<Long, List<SysPermission>> childrenMap = all.stream()
                .filter(p -> p.getParentId() != null && p.getParentId() > 0)
                .collect(Collectors.groupingBy(SysPermission::getParentId));
        List<SysPermission> roots = new ArrayList<>();
        for (SysPermission p : all) {
            if (p.getParentId() == null || p.getParentId() == 0) {
                p.setChildren(childrenMap.getOrDefault(p.getId(), new ArrayList<>()));
                roots.add(p);
            }
        }
        return Result.success(roots);
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色已有权限ID列表")
    public Result<List<Long>> getRolePermissionIds(@PathVariable Long roleId) {
        return Result.success(permissionMapper.findPermissionIdsByRoleId(roleId));
    }
}
