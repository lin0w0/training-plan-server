package com.hfk.training.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.course.entity.Course;
import com.hfk.training.modules.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程库增删改查、先修关系")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    @Operation(summary = "分页查询课程")
    public Result<PageResult<Course>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String courseType,
            @RequestParam(required = false) String courseCategory) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(courseCode != null, Course::getCourseCode, courseCode)
                .like(courseName != null, Course::getCourseName, courseName)
                .eq(courseType != null, Course::getCourseType, courseType)
                .eq(courseCategory != null, Course::getCourseCategory, courseCategory)
                .orderByAsc(Course::getCourseCode);
        Page<Course> result = courseService.page(new Page<>(page, pageSize), wrapper);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有课程")
    public Result<List<Course>> all() {
        return Result.success(courseService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情")
    public Result<Course> getById(@PathVariable Long id) {
        return Result.success(courseService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增课程")
    @PreAuthorize("hasAnyAuthority('course:add','ROLE_ADMIN')")
    public Result<Void> create(@RequestBody Course course) {
        // 唯一性校验
        Long count = courseService.lambdaQuery()
                .eq(Course::getCourseCode, course.getCourseCode()).count();
        if (count > 0) return Result.badRequest("课程代码已存在");
        count = courseService.lambdaQuery()
                .eq(Course::getCourseName, course.getCourseName()).count();
        if (count > 0) return Result.badRequest("课程名称已存在");
        courseService.save(course);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新课程")
    @PreAuthorize("hasAnyAuthority('course:edit','ROLE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Course course) {
        course.setId(id);
        courseService.updateById(course);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程")
    @PreAuthorize("hasAnyAuthority('course:delete','ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.ok("删除成功");
    }
}
