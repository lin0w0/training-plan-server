package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.College;
import com.hfk.training.modules.system.mapper.CollegeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/college")
@RequiredArgsConstructor
@Tag(name = "学院管理", description = "学院增删改查")
public class CollegeController {

    private final CollegeMapper collegeMapper;

    @GetMapping("/page")
    @Operation(summary = "分页查询学院")
    public Result<PageResult<College>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String collegeName) {
        LambdaQueryWrapper<College> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(collegeName), College::getCollegeName, collegeName)
                .orderByAsc(College::getSortOrder);
        Page<College> result = collegeMapper.selectPage(new Page<>(page, pageSize), w);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有学院")
    public Result<List<College>> all() {
        return Result.success(collegeMapper.selectList(new LambdaQueryWrapper<College>().orderByAsc(College::getSortOrder)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学院详情")
    public Result<College> getById(@PathVariable Long id) {
        return Result.success(collegeMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增学院")
    public Result<Void> create(@RequestBody College college) {
        collegeMapper.insert(college);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学院")
    public Result<Void> update(@PathVariable Long id, @RequestBody College college) {
        college.setId(id);
        collegeMapper.updateById(college);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学院")
    public Result<Void> delete(@PathVariable Long id) {
        collegeMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
