package com.hfk.training.modules.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.student.entity.AcademicWarning;
import com.hfk.training.modules.student.service.WarningGenerator;
import com.hfk.training.modules.student.service.WarningService;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.mapper.StudentMapper;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/warning")
@RequiredArgsConstructor
@Tag(name = "学业预警管理", description = "预警查询、生成、处理")
public class WarningController {

    private final WarningService warningService;
    private final WarningGenerator warningGenerator;
    private final SysUserMapper sysUserMapper;
    private final StudentMapper studentMapper;

    @GetMapping("/page")
    @Operation(summary = "分页查询预警（学生只看自己的）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String warningLevel,
            @RequestParam(required = false) Integer isResolved) {
        // 学生只能看自己的预警
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        Long studentId = null;
        if (user != null && "student".equals(user.getUserType())) {
            Student student = studentMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId()));
            if (student != null) studentId = student.getId();
        }
        Page<Map<String, Object>> result = warningService.pageWarningsWithNames(page, pageSize, warningLevel, isResolved, studentId);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "查询学生预警记录")
    public Result<?> getStudentWarnings(@PathVariable Long studentId) {
        return Result.success(warningService.getWarningsByStudentId(studentId));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "处理预警")
    public Result<Void> resolve(@PathVariable Long id, @RequestBody ResolveRequest request) {
        warningService.resolveWarning(id, request.getRemark());
        return Result.ok("预警已处理");
    }

    @PostMapping("/generate")
    @Operation(summary = "自动生成预警")
    public Result<Map<String, Integer>> generate() {
        Map<String, Integer> result = warningGenerator.generateAll();
        return Result.success("预警生成完成", result);
    }

    @Data
    public static class ResolveRequest {
        private String remark;
    }
}
