package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.Teacher;
import com.hfk.training.modules.system.mapper.TeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system/teacher")
@RequiredArgsConstructor
@Tag(name = "教师管理", description = "教师增删改查")
public class TeacherController {

    private final TeacherMapper teacherMapper;

    @GetMapping("/page")
    @Operation(summary = "分页查询教师（含学院名称）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long collegeId) {
        Page<Map<String, Object>> result = teacherMapper.pageWithNames(
                new Page<>(page, pageSize), realName, collegeId);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取教师详情")
    public Result<Teacher> getById(@PathVariable Long id) {
        return Result.success(teacherMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增教师")
    public Result<Void> create(@RequestBody Teacher teacher) {
        teacherMapper.insert(teacher);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教师")
    public Result<Void> update(@PathVariable Long id, @RequestBody Teacher teacher) {
        teacher.setId(id);
        teacherMapper.updateById(teacher);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教师")
    public Result<Void> delete(@PathVariable Long id) {
        teacherMapper.deleteById(id);
        return Result.ok("删除成功");
    }
}
