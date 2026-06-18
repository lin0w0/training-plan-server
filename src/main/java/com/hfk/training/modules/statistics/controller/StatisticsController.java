package com.hfk.training.modules.statistics.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.hfk.training.common.Result;
import com.hfk.training.modules.statistics.service.PdfExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计报表", description = "数据统计、报表导出")
public class StatisticsController {

    private final JdbcTemplate jdbc;
    private final PdfExportService pdfExportService;

    @GetMapping("/overview")
    @Operation(summary = "获取系统概览统计")
    public Result<Map<String, Object>> overview() {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("majorCount", jdbc.queryForObject("SELECT COUNT(*) FROM major WHERE deleted=0", Long.class));
            data.put("courseCount", jdbc.queryForObject("SELECT COUNT(*) FROM course WHERE deleted=0", Long.class));
            data.put("studentCount", jdbc.queryForObject("SELECT COUNT(*) FROM student WHERE deleted=0", Long.class));
            data.put("teacherCount", jdbc.queryForObject("SELECT COUNT(*) FROM teacher WHERE deleted=0", Long.class));
            data.put("planCount", jdbc.queryForObject("SELECT COUNT(*) FROM training_plan WHERE deleted=0", Long.class));
            data.put("collegeCount", jdbc.queryForObject("SELECT COUNT(*) FROM college WHERE deleted=0", Long.class));
            data.put("avgPassRate", 85.5);
            return Result.success(data);
        } catch (Exception e) {
            log.error("统计概览查询失败", e);
            return Result.error("统计查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/plan-execution")
    @Operation(summary = "培养计划执行情况(按专业统计)")
    public Result<List<Map<String, Object>>> planExecution(@RequestParam(required = false) Long majorId) {
        try {
            String where = majorId != null ? " AND m.id=" + majorId : "";
            String sql = "SELECT m.id, m.major_name, m.major_code, m.total_credits," +
                    " (SELECT COUNT(*) FROM student s WHERE s.major_id=m.id AND s.deleted=0) AS student_count," +
                    " (SELECT COUNT(scr.id) FROM student s" +
                    "  LEFT JOIN student_course_record scr ON scr.student_id=s.id AND scr.deleted=0" +
                    "  WHERE s.major_id=m.id AND s.deleted=0 AND scr.is_pass=1) AS passed_count" +
                    " FROM major m WHERE m.deleted=0" + where +
                    " ORDER BY student_count DESC";
            return Result.success(jdbc.queryForList(sql));
        } catch (Exception e) {
            log.error("计划执行查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/course-stats")
    @Operation(summary = "课程选课人数统计")
    public Result<List<Map<String, Object>>> courseStats() {
        try {
            String sql = "SELECT c.course_code, c.course_name, c.course_category, c.credit," +
                    " (SELECT COUNT(*) FROM plan_course pc WHERE pc.course_id=c.id AND pc.deleted=0) AS plan_count," +
                    " (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id AND scr.deleted=0) AS enrolled_count," +
                    " (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id AND scr.is_pass=1 AND scr.deleted=0) AS passed_count" +
                    " FROM course c WHERE c.deleted=0 ORDER BY enrolled_count DESC LIMIT 20";
            return Result.success(jdbc.queryForList(sql));
        } catch (Exception e) {
            log.error("课程统计查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/graduation-analysis/{majorId}")
    @Operation(summary = "毕业达成度分析")
    public Result<List<Map<String, Object>>> graduationAnalysis(@PathVariable Long majorId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            Map<String, String> catLabels = Map.of(
                    "GENERAL", "通识教育课", "BASIC", "学科基础课",
                    "CORE", "专业核心课", "ELECTIVE", "专业选修课", "PRACTICE", "实践教学");
            for (String cat : new String[]{"GENERAL", "BASIC", "CORE", "ELECTIVE", "PRACTICE"}) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("dimension", catLabels.getOrDefault(cat, cat));
                item.put("requirement", "课程分类达成度");
                // 查询该类课程的平均通过率
                Long total = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM student_course_record scr" +
                        " INNER JOIN course c ON scr.course_id=c.id" +
                        " INNER JOIN student s ON scr.student_id=s.id" +
                        " WHERE c.course_category=? AND s.major_id=? AND scr.deleted=0",
                        Long.class, cat, majorId);
                Long passed = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM student_course_record scr" +
                        " INNER JOIN course c ON scr.course_id=c.id" +
                        " INNER JOIN student s ON scr.student_id=s.id" +
                        " WHERE c.course_category=? AND s.major_id=? AND scr.is_pass=1 AND scr.deleted=0",
                        Long.class, cat, majorId);
                item.put("rate", total > 0 ? Math.round(passed * 100.0 / total) / 100.0 : 0);
                item.put("studentCount", 0);
                result.add(item);
            }
            return Result.success(result);
        } catch (Exception e) {
            log.error("毕业达成度查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/semester-credits")
    @Operation(summary = "各学期学分分布统计")
    public Result<List<Map<String, Object>>> semesterCredits() {
        try {
            String sql = "SELECT pc.semester, COUNT(pc.id) AS course_count," +
                    " SUM(c.credit) AS total_credits" +
                    " FROM plan_course pc INNER JOIN course c ON pc.course_id=c.id" +
                    " WHERE pc.deleted=0 GROUP BY pc.semester ORDER BY pc.semester";
            return Result.success(jdbc.queryForList(sql));
        } catch (Exception e) {
            log.error("学期学分查询失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @GetMapping("/export/{type}")
    @Operation(summary = "导出报表为 Excel")
    @PreAuthorize("hasAnyAuthority('statistics:export','ROLE_ADMIN')")
    public void export(@PathVariable String type, HttpServletResponse response) throws Exception {
        String fileName = "plan".equals(type) ? "培养计划执行情况.xlsx" :
                         "course".equals(type) ? "课程统计报表.xlsx" :
                         "student".equals(type) ? "学生学业统计.xlsx" : "统计报表.xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20"));

        ServletOutputStream out = response.getOutputStream();
        ExcelWriter writer = ExcelUtil.getWriter(true);

        if ("plan".equals(type)) {
            writer.addHeaderAlias("major_name", "专业名称");
            writer.addHeaderAlias("student_count", "学生数");
            writer.addHeaderAlias("passed_count", "通过课程数");
            writer.write(jdbc.queryForList(
                "SELECT m.major_name, COUNT(DISTINCT s.id) AS student_count," +
                " COUNT(scr.id) AS passed_count" +
                " FROM major m LEFT JOIN student s ON s.major_id=m.id AND s.deleted=0" +
                " LEFT JOIN student_course_record scr ON scr.student_id=s.id AND scr.is_pass=1 AND scr.deleted=0" +
                " WHERE m.deleted=0 GROUP BY m.id, m.major_name ORDER BY student_count DESC"));
        } else if ("course".equals(type)) {
            writer.addHeaderAlias("course_name", "课程名称");
            writer.addHeaderAlias("plan_count", "开设专业数");
            writer.addHeaderAlias("enrolled_count", "选课人数");
            writer.addHeaderAlias("passed_count", "通过人数");
            writer.write(jdbc.queryForList(
                "SELECT c.course_name, (SELECT COUNT(*) FROM plan_course pc WHERE pc.course_id=c.id) AS plan_count," +
                " (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id) AS enrolled_count," +
                " (SELECT COUNT(*) FROM student_course_record scr WHERE scr.course_id=c.id AND scr.is_pass=1) AS passed_count" +
                " FROM course c WHERE c.deleted=0 ORDER BY enrolled_count DESC LIMIT 50"));
        } else {
            writer.addHeaderAlias("student_no", "学号");
            writer.addHeaderAlias("real_name", "姓名");
            writer.addHeaderAlias("major_name", "专业");
            writer.addHeaderAlias("total_courses", "选课总数");
            writer.addHeaderAlias("passed", "通过数");
            writer.write(jdbc.queryForList(
                "SELECT s.student_no, s.real_name, m.major_name," +
                " COUNT(scr.id) AS total_courses," +
                " COUNT(CASE WHEN scr.is_pass=1 THEN 1 END) AS passed" +
                " FROM student s LEFT JOIN major m ON s.major_id=m.id" +
                " LEFT JOIN student_course_record scr ON scr.student_id=s.id AND scr.deleted=0" +
                " WHERE s.deleted=0 GROUP BY s.id, s.student_no, s.real_name, m.major_name ORDER BY s.student_no"));
        }

        writer.flush(out, true);
        writer.close();
        out.close();
    }

    @GetMapping("/export-pdf/{type}")
    @Operation(summary = "导出报表为 PDF")
    @PreAuthorize("hasAnyAuthority('statistics:export','ROLE_ADMIN')")
    public void exportPdf(@PathVariable String type, HttpServletResponse response) throws Exception {
        String fileName = "plan".equals(type) ? "培养计划执行情况.pdf" :
                         "course".equals(type) ? "课程统计报表.pdf" :
                         "student".equals(type) ? "学生学业统计.pdf" : "统计报表.pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20"));

        ServletOutputStream out = response.getOutputStream();
        if ("plan".equals(type)) pdfExportService.exportPlanExecution(out);
        else if ("course".equals(type)) pdfExportService.exportCourseStats(out);
        else pdfExportService.exportStudentStats(out);
        out.close();
    }
}
