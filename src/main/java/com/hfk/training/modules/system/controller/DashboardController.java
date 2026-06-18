package com.hfk.training.modules.system.controller;

import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.entity.Teacher;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import com.hfk.training.modules.system.mapper.StudentMapper;
import com.hfk.training.modules.system.mapper.TeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class DashboardController {

    private final JdbcTemplate jdbc;
    private final SysUserMapper sysUserMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;

    @GetMapping("/pending-items")
    @Operation(summary = "获取当前用户待处理事项")
    public Result<List<Map<String, Object>>> getPendingItems() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        if (user == null) return Result.success(List.of());

        List<Map<String, Object>> items = new ArrayList<>();

        if ("admin".equals(user.getUserType())) {
            // 管理员：待审核大纲 + 待发布计划
            Long syllabusCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM teaching_syllabus WHERE status='已提交' AND deleted=0", Long.class);
            if (syllabusCount != null && syllabusCount > 0) {
                items.add(Map.of("title", syllabusCount + " 份教学大纲待审核", "time", "今日", "urgent", true, "path", "/course/syllabus"));
            }
            Long planCount = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM training_plan WHERE status='DRAFT' AND deleted=0", Long.class);
            if (planCount != null && planCount > 0) {
                items.add(Map.of("title", planCount + " 个培养计划待发布", "time", "今日", "urgent", false, "path", "/training-plan"));
            }
        } else if ("teacher".equals(user.getUserType())) {
            // 教师：被驳回的大纲 + 待提交大纲
            Teacher teacher = teacherMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Teacher>()
                            .eq(Teacher::getUserId, user.getId()));
            if (teacher != null) {
                Long rejected = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM teaching_syllabus WHERE teacher_id=? AND status='已驳回' AND deleted=0",
                        Long.class, teacher.getId());
                if (rejected != null && rejected > 0) {
                    items.add(Map.of("title", rejected + " 份大纲被驳回需修改", "time", "今日", "urgent", true, "path", "/course/syllabus"));
                }
                Long draft = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM teaching_syllabus WHERE teacher_id=? AND (status='草稿' OR status IS NULL) AND deleted=0",
                        Long.class, teacher.getId());
                if (draft != null && draft > 0) {
                    items.add(Map.of("title", draft + " 份大纲待提交", "time", "今日", "urgent", false, "path", "/course/syllabus"));
                }
            }
        } else if ("student".equals(user.getUserType())) {
            // 学生：学业预警 + 挂科提醒
            Student student = studentMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Student>()
                            .eq(Student::getUserId, user.getId()));
            if (student != null) {
                Long warnings = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM academic_warning WHERE student_id=? AND is_resolved=0 AND deleted=0",
                        Long.class, student.getId());
                if (warnings != null && warnings > 0) {
                    items.add(Map.of("title", "您有 " + warnings + " 条学业预警待处理", "time", "今日", "urgent", true, "path", "/student/warning"));
                }
                Long failed = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM student_course_record WHERE student_id=? AND is_pass=0 AND deleted=0",
                        Long.class, student.getId());
                if (failed != null && failed > 0) {
                    items.add(Map.of("title", "您有 " + failed + " 门课程未通过", "time", "近期", "urgent", true, "path", "/student/progress"));
                }
            }
        }

        if (items.isEmpty()) {
            items.add(Map.of("title", "暂无待处理事项", "time", "", "urgent", false, "path", "/dashboard"));
        }
        return Result.success(items);
    }
}
