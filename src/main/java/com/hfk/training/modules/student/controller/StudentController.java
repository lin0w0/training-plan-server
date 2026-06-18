package com.hfk.training.modules.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.student.entity.AcademicWarning;
import com.hfk.training.modules.course.entity.Course;
import com.hfk.training.modules.course.mapper.CourseMapper;
import com.hfk.training.modules.plan.entity.PlanCourse;
import com.hfk.training.modules.plan.mapper.PlanCourseMapper;
import com.hfk.training.modules.student.entity.StudentCourseRecord;
import com.hfk.training.modules.student.mapper.StudentCourseRecordMapper;
import com.hfk.training.modules.student.service.StudentService;
import com.hfk.training.modules.system.entity.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
@Tag(name = "学生学业管理", description = "学生选课、修读进度、学业预警")
public class StudentController {

    private final StudentService studentService;
    private final StudentCourseRecordMapper recordMapper;
    private final PlanCourseMapper planCourseMapper;
    private final CourseMapper courseMapper;
    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/page")
    @Operation(summary = "分页查询学生")
    public Result<PageResult<Student>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Student> result = studentService.page(new Page<>(page, pageSize));
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    // ========== 选课记录 ==========
    @GetMapping("/{studentId}/courses")
    @Operation(summary = "查询学生选课记录")
    public Result<List<StudentCourseRecord>> getStudentCourses(@PathVariable Long studentId) {
        return Result.success(studentService.getStudentCourses(studentId));
    }

    @PostMapping("/course-record")
    @Operation(summary = "新增选课记录")
    public Result<Void> addCourseRecord(@RequestBody StudentCourseRecord record) {
        // 检查是否已存在(含已物理删除的残留)
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student_course_record WHERE student_id=? AND course_id=? AND semester=?",
                Integer.class, record.getStudentId(), record.getCourseId(), record.getSemester());
        if (count != null && count > 0) return Result.badRequest("该学生此课程记录已存在");
        studentService.addCourseRecord(record);
        return Result.ok("添加成功");
    }

    @PutMapping("/course-record/{id}")
    @Operation(summary = "更新选课记录（成绩/状态）")
    public Result<Void> updateCourseRecord(@PathVariable Long id, @RequestBody StudentCourseRecord record) {
        StudentCourseRecord exist = recordMapper.selectById(id);
        if (exist == null) return Result.error("记录不存在");
        exist.setScore(record.getScore());
        exist.setIsPass(record.getIsPass() != null ? record.getIsPass() : (record.getScore() != null && record.getScore().doubleValue() >= 60 ? 1 : 0));
        recordMapper.updateById(exist);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/course-record/{id}")
    @Operation(summary = "删除选课记录")
    public Result<Void> deleteCourseRecord(@PathVariable Long id) {
        jdbcTemplate.update("DELETE FROM student_course_record WHERE id=?", id);
        return Result.ok("删除成功");
    }

    // ========== 修读进度 ==========
    @GetMapping("/{studentId}/progress")
    @Operation(summary = "获取学生修读进度")
    public Result<Map<String, Object>> getProgress(@PathVariable Long studentId) {
        return Result.success(studentService.getStudentProgress(studentId));
    }

    @GetMapping("/my-progress")
    @Operation(summary = "获取当前学生(本人)修读进度")
    public Result<Map<String, Object>> getMyProgress() {
        // TODO: 从 SecurityContext 获取当前学生ID
        return Result.success(studentService.getStudentProgress(1L));
    }

    @GetMapping("/my-plan")
    @Operation(summary = "获取当前学生的培养计划视图(按学期展示,标记已修/未修)")
    public Result<Map<String, Object>> getMyPlan(@RequestParam(defaultValue = "1") Long studentId) {
        // 1. 查询学生信息
        Student student = studentService.getById(studentId);
        if (student == null) return Result.error("学生不存在");

        // 2. 查询学生的选课记录
        List<StudentCourseRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<StudentCourseRecord>()
                        .eq(StudentCourseRecord::getStudentId, studentId));
        Set<Long> passedCourseIds = new HashSet<>();
        Set<Long> failedCourseIds = new HashSet<>();
        for (StudentCourseRecord r : records) {
            if (r.getIsPass() != null && r.getIsPass() == 1) passedCourseIds.add(r.getCourseId());
            else if (r.getIsPass() != null && r.getIsPass() == 0) failedCourseIds.add(r.getCourseId());
        }

        // 3. 按学期组织，标记状态
        Map<Integer, List<Map<String, Object>>> semesters = new LinkedHashMap<>();
        for (int s = 1; s <= 8; s++) semesters.put(s, new ArrayList<>());

        int totalRequired = 0, earnedCredits = 0;
        // Simplified: iterate records
        for (StudentCourseRecord r : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("courseId", r.getCourseId());

            // 获取课程详情
            Course course = courseMapper.selectById(r.getCourseId());
            if (course != null) {
                item.put("courseName", course.getCourseName());
                item.put("courseCode", course.getCourseCode());
                item.put("credit", course.getCredit());
                item.put("category", course.getCourseCategory());
            }

            item.put("semester", r.getSemester());
            item.put("score", r.getScore());
            item.put("status", r.getIsPass() == 1 ? "passed" : (r.getIsPass() == 0 ? "failed" : "ongoing"));
            String semStr = r.getSemester() != null ? r.getSemester().replaceAll("[^0-9]", "") : "1";
            int sem = semStr.isEmpty() ? 1 : Integer.parseInt(semStr.substring(semStr.length() - 1));
            semesters.get(sem).add(item);
            if (r.getIsPass() == 1) earnedCredits += 3; // approx
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("student", student);
        result.put("semesters", semesters);
        result.put("earnedCredits", earnedCredits);
        result.put("totalRequired", 160);
        result.put("completionRate", Math.min(100, earnedCredits * 100 / 160));
        return Result.success(result);
    }

    // ========== 选课记录导入 ==========
    @PostMapping("/courses/import")
    @Operation(summary = "Excel批量导入选课记录")
    public Result<Map<String, Object>> importRecords(@RequestParam("file") MultipartFile file,
                                                      @RequestParam Long studentId) {
        int success = 0, fail = 0;
        try (var reader = cn.hutool.poi.excel.ExcelUtil.getReader(file.getInputStream())) {
            List<Map<String, Object>> rows = reader.readAll();
            for (Map<String, Object> row : rows) {
                try {
                    StudentCourseRecord r = new StudentCourseRecord();
                    r.setStudentId(studentId);
                    r.setCourseId(Long.parseLong(row.getOrDefault("courseId", "0").toString()));
                    r.setSemester(row.getOrDefault("semester", "2023-1").toString());
                    Object scoreObj = row.get("score");
                    if (scoreObj != null) r.setScore(new BigDecimal(scoreObj.toString()));
                    r.setStatus("已修");
                    Object isPass = row.get("isPass");
                    r.setIsPass(isPass != null ? Integer.parseInt(isPass.toString()) : 1);
                    recordMapper.insert(r);
                    success++;
                } catch (Exception e) { fail++; }
            }
        } catch (Exception e) { return Result.error("文件解析失败: " + e.getMessage()); }
        return Result.success(Map.of("success", success, "fail", fail));
    }
}
