package com.hfk.training.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.entity.Teacher;
import com.hfk.training.modules.system.mapper.StudentMapper;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import com.hfk.training.modules.system.mapper.TeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生学籍管理 Controller (独立于学业的 StudentController)
 */
@RestController
@RequestMapping("/system/student")
@RequiredArgsConstructor
@Tag(name = "学生管理", description = "学生学籍信息增删改查")
public class StudentManageController {

    private final StudentMapper studentMapper;
    private final SysUserMapper sysUserMapper;
    private final TeacherMapper teacherMapper;
    private final JdbcTemplate jdbc;

    @GetMapping("/page")
    @Operation(summary = "分页查询学生（班主任只看本班）")
    public Result<PageResult<Map<String, Object>>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer enrollmentYear) {
        // 班主任只能看自己班级的学生
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user != null && "teacher".equals(user.getUserType())) {
            Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId, user.getId()));
            if (teacher != null) {
                List<Long> classIds = jdbc.queryForList(
                        "SELECT id FROM class_info WHERE head_teacher = ? AND deleted = 0",
                        Long.class, teacher.getRealName());
                if (!classIds.isEmpty()) {
                    classId = classIds.get(0); // 强制只看自己班
                    collegeId = null; majorId = null; // 清除其他筛选
                }
            }
        }
        Page<Map<String, Object>> result = studentMapper.pageWithNames(
                new Page<>(page, pageSize), studentNo, realName, collegeId, majorId, classId, enrollmentYear);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有学生(下拉)")
    public Result<List<Student>> all(@RequestParam(required = false) Long classId) {
        LambdaQueryWrapper<Student> w = new LambdaQueryWrapper<>();
        w.eq(classId != null, Student::getClassId, classId)
                .orderByAsc(Student::getStudentNo);
        return Result.success(studentMapper.selectList(w));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取学生详情")
    public Result<Student> getById(@PathVariable Long id) {
        return Result.success(studentMapper.selectById(id));
    }

    @PostMapping
    @Operation(summary = "新增学生")
    @PreAuthorize("hasAnyAuthority('data:student:add','ROLE_ADMIN')")
    public Result<Void> create(@RequestBody Student student) {
        // 学号唯一性校验（含已删除记录）
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM student WHERE student_no=?", Long.class, student.getStudentNo());
        if (count != null && count > 0) return Result.badRequest("学号已存在");
        studentMapper.insert(student);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新学生")
    @PreAuthorize("hasAnyAuthority('data:student:edit','ROLE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        studentMapper.updateById(student);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生")
    @PreAuthorize("hasAnyAuthority('data:student:delete','ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        jdbc.update("DELETE FROM student WHERE id=?", id);
        return Result.ok("删除成功");
    }
}
