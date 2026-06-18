package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.ClassInfo;
import com.hfk.training.modules.system.mapper.ClassInfoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/class")
@RequiredArgsConstructor
@Tag(name = "班级管理", description = "班级增删改查")
public class ClassInfoController {

    private final ClassInfoMapper classInfoMapper;

    @GetMapping("/page")
    @Operation(summary = "分页查询班级（含专业名称）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String classCode,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) Integer grade) {
        Page<Map<String, Object>> result = classInfoMapper.pageWithNames(
                new Page<>(page, pageSize), classCode, className, majorId, grade);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有班级")
    public Result<List<ClassInfo>> all(@RequestParam(required = false) Long majorId) {
        LambdaQueryWrapper<ClassInfo> w = new LambdaQueryWrapper<>();
        w.eq(majorId != null, ClassInfo::getMajorId, majorId);
        return Result.success(classInfoMapper.selectList(w));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取班级详情")
    public Result<ClassInfo> getById(@PathVariable Long id) {
        return Result.success(classInfoMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增班级")
    public Result<Void> create(@RequestBody ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新班级")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        classInfo.setId(id);
        classInfoMapper.updateById(classInfo);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
