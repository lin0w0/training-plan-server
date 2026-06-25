package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.Major;
import com.hfk.training.modules.system.mapper.MajorMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/major")
@RequiredArgsConstructor
@Tag(name = "专业管理", description = "专业增删改查")
public class MajorController {

    private final MajorMapper majorMapper;
    private final JdbcTemplate jdbc;

    @GetMapping("/page")
    @Operation(summary = "分页查询专业（含学院名称）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String majorCode,
            @RequestParam(required = false) String majorName,
            @RequestParam(required = false) Long collegeId) {
        Page<Map<String, Object>> result = majorMapper.pageWithNames(
                new Page<>(page, pageSize), majorCode, majorName, collegeId);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有专业")
    public Result<List<Major>> all(@RequestParam(required = false) Long collegeId) {
        LambdaQueryWrapper<Major> w = new LambdaQueryWrapper<>();
        w.eq(collegeId != null, Major::getCollegeId, collegeId);
        return Result.success(majorMapper.selectList(w));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取专业详情")
    public Result<Major> getById(@PathVariable Long id) {
        return Result.success(majorMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增专业")
    public Result<Void> create(@RequestBody Major major) {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM major WHERE major_code=?", Long.class, major.getMajorCode());
        if (count != null && count > 0) return Result.badRequest("专业代码已存在");
        majorMapper.insert(major);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新专业")
    public Result<Void> update(@PathVariable Long id, @RequestBody Major major) {
        major.setId(id);
        majorMapper.updateById(major);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除专业")
    public Result<Void> delete(@PathVariable Long id) {
        jdbc.update("DELETE FROM major WHERE id=?", id);
        return Result.ok("删除成功");
    }
}
