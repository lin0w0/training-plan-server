package com.hfk.training.modules.course.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.course.entity.TeachingSyllabus;
import com.hfk.training.modules.course.mapper.SyllabusMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.hfk.training.common.BusinessException;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.entity.Teacher;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import com.hfk.training.modules.system.mapper.TeacherMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/syllabus")
@RequiredArgsConstructor
@Tag(name = "教学大纲管理", description = "教学大纲增删改查")
public class SyllabusController {

    private final SyllabusMapper syllabusMapper;
    private final SysUserMapper sysUserMapper;
    private final TeacherMapper teacherMapper;

    @GetMapping("/page")
    @Operation(summary = "分页查询教学大纲（含课程名和教师名，教师只看自己的）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long courseId) {
        // 教师只能看自己的大纲
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        Long teacherId = null;
        if (user != null && "teacher".equals(user.getUserType())) {
            Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
            if (teacher != null) teacherId = teacher.getId();
        }
        Page<Map<String, Object>> result = syllabusMapper.pageWithNames(new Page<>(page, pageSize), courseId, teacherId);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取某课程的教学大纲")
    public Result<?> getByCourseId(@PathVariable Long courseId) {
        return Result.success(syllabusMapper.selectList(
                new LambdaQueryWrapper<TeachingSyllabus>().eq(TeachingSyllabus::getCourseId, courseId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取大纲详情")
    public Result<TeachingSyllabus> getById(@PathVariable Long id) {
        return Result.success(syllabusMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增教学大纲")
    public Result<Void> create(@RequestBody TeachingSyllabus syllabus) {
        // 教师自动关联
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user != null && "teacher".equals(user.getUserType())) {
            Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
            if (teacher != null) syllabus.setTeacherId(teacher.getId());
        }
        syllabusMapper.insert(syllabus);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新教学大纲(仅草稿/已驳回可编辑)")
    public Result<Void> update(@PathVariable Long id, @RequestBody TeachingSyllabus syllabus) {
        TeachingSyllabus exist = syllabusMapper.selectById(id);
        if (exist == null) return Result.error("大纲不存在");
        if (!"草稿".equals(exist.getStatus()) && !"已驳回".equals(exist.getStatus())) {
            return Result.badRequest("当前状态不允许编辑，仅草稿或已驳回状态可修改");
        }
        syllabus.setId(id);
        syllabusMapper.updateById(syllabus);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除教学大纲")
    @PreAuthorize("hasAnyAuthority('syllabus:delete','ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        syllabusMapper.deleteById(id);
        return Result.ok("删除成功");
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "教师提交大纲审核")
    public Result<Void> submit(@PathVariable Long id) {
        TeachingSyllabus s = syllabusMapper.selectById(id);
        if (s == null) throw new BusinessException("大纲不存在");
        if (!"草稿".equals(s.getStatus()) && s.getStatus() != null) throw new BusinessException("只有草稿状态可提交");
        s.setStatus("已提交");
        syllabusMapper.updateById(s);
        return Result.ok("提交成功，等待审核");
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "管理员审核大纲")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Result<Void> approve(@PathVariable Long id, @RequestParam(defaultValue = "APPROVE") String action) {
        TeachingSyllabus s = syllabusMapper.selectById(id);
        if (s == null) throw new BusinessException("大纲不存在");
        if (!"已提交".equals(s.getStatus())) throw new BusinessException("只有已提交状态可审核");
        s.setStatus("APPROVE".equals(action) ? "已审核" : "已驳回");
        syllabusMapper.updateById(s);
        return Result.ok("APPROVE".equals(action) ? "审核通过" : "已驳回");
    }
}
