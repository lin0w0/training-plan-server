package com.hfk.training.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfk.training.common.Result;
import com.hfk.training.modules.course.entity.CoursePrerequisite;
import com.hfk.training.modules.course.mapper.CoursePrerequisiteMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "课程先修管理", description = "课程先修/后继关系管理")
public class CoursePrerequisiteController {

    private final CoursePrerequisiteMapper prerequisiteMapper;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/{courseId}/prerequisites")
    @Operation(summary = "查询课程的所有先修课程")
    public Result<List<Map<String, Object>>> getPrerequisites(@PathVariable Long courseId) {
        return Result.success(prerequisiteMapper.findPrerequisitesByCourseId(courseId));
    }

    @GetMapping("/{courseId}/dependents")
    @Operation(summary = "查询依赖该课程的后继课程")
    public Result<List<Map<String, Object>>> getDependents(@PathVariable Long courseId) {
        return Result.success(prerequisiteMapper.findDependentsByCourseId(courseId));
    }

    @PostMapping("/{courseId}/prerequisite")
    @Operation(summary = "添加先修课程")
    @PreAuthorize("hasAnyAuthority('course:prerequisite','ROLE_ADMIN')")
    public Result<Void> addPrerequisite(@PathVariable Long courseId, @RequestBody AddPrereqRequest request) {
        // 检查是否已存在
        Long count = prerequisiteMapper.selectCount(new LambdaQueryWrapper<CoursePrerequisite>()
                .eq(CoursePrerequisite::getCourseId, courseId)
                .eq(CoursePrerequisite::getPrerequisiteId, request.getPrerequisiteId()));
        if (count > 0) return Result.ok("已存在该先修关系");

        CoursePrerequisite cp = new CoursePrerequisite();
        cp.setCourseId(courseId);
        cp.setPrerequisiteId(request.getPrerequisiteId());
        cp.setIsStrict(request.getIsStrict() != null ? request.getIsStrict() : 1);
        prerequisiteMapper.insert(cp);
        return Result.ok("添加成功");
    }

    @DeleteMapping("/{courseId}/prerequisite/{prerequisiteId}")
    @Operation(summary = "删除先修课程关系")
    @PreAuthorize("hasAnyAuthority('course:prerequisite','ROLE_ADMIN')")
    public Result<Void> removePrerequisite(@PathVariable Long courseId, @PathVariable Long prerequisiteId) {
        jdbcTemplate.update("DELETE FROM course_prerequisite WHERE course_id=? AND prerequisite_id=?", courseId, prerequisiteId);
        return Result.ok("删除成功");
    }

    /**
     * 批量保存课程的先修关系（替换模式）
     */
    @PutMapping("/{courseId}/prerequisites")
    @Operation(summary = "批量保存先修关系")
    @PreAuthorize("hasAnyAuthority('course:prerequisite','ROLE_ADMIN')")
    public Result<Void> savePrerequisites(@PathVariable Long courseId, @RequestBody SavePrereqsRequest request) {
        // 物理删除旧关系（避免逻辑删除导致唯一约束冲突）
        jdbcTemplate.update("DELETE FROM course_prerequisite WHERE course_id = ?", courseId);
        if (request.getPrerequisiteIds() != null) {
            for (Long prereqId : request.getPrerequisiteIds()) {
                CoursePrerequisite cp = new CoursePrerequisite();
                cp.setCourseId(courseId);
                cp.setPrerequisiteId(prereqId);
                cp.setIsStrict(1);
                prerequisiteMapper.insert(cp);
            }
        }
        return Result.ok("保存成功");
    }

    @Data
    public static class AddPrereqRequest {
        private Long prerequisiteId;
        private Integer isStrict;
    }

    @Data
    public static class SavePrereqsRequest {
        private List<Long> prerequisiteIds;
    }
}
