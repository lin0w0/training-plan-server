package com.hfk.training.modules.course.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.hfk.training.common.Result;
import com.hfk.training.modules.course.entity.Course;
import com.hfk.training.modules.course.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
@Tag(name = "课程导入", description = "课程Excel批量导入")
public class CourseImportController {

    private final CourseMapper courseMapper;

    @PostMapping("/import")
    @Operation(summary = "Excel批量导入课程")
    @PreAuthorize("hasAnyAuthority('course:import','ROLE_ADMIN')")
    public Result<Map<String, Object>> importCourses(@RequestParam("file") MultipartFile file) {
        int success = 0, fail = 0;
        List<String> errors = new ArrayList<>();

        try (ExcelReader reader = ExcelUtil.getReader(file.getInputStream())) {
            List<Map<String, Object>> rows = reader.readAll();
            for (int i = 0; i < rows.size(); i++) {
                Map<String, Object> row = rows.get(i);
                try {
                    Course course = new Course();
                    course.setCourseCode(getString(row, "courseCode", "课程代码"));
                    course.setCourseName(getString(row, "courseName", "课程名称"));
                    course.setCredit(new BigDecimal(getString(row, "credit", "学分")));
                    course.setClassHour(Integer.parseInt(getString(row, "classHour", "总学时")));
                    course.setLectureHour(Integer.parseInt(getString(row, "lectureHour", "理论学时")));
                    course.setLabHour(Integer.parseInt(getString(row, "labHour", "实践学时")));
                    course.setCourseType(getString(row, "courseType", "课程性质"));
                    course.setCourseCategory(getString(row, "courseCategory", "课程分类"));
                    course.setSemester(Integer.parseInt(getString(row, "semester", "建议学期")));
                    course.setExamType(getString(row, "examType", "考核方式"));
                    course.setDescription(getString(row, "description", "课程简介"));
                    course.setStatus(1);

                    courseMapper.insert(course);
                    success++;
                } catch (Exception e) {
                    fail++;
                    errors.add("第" + (i + 2) + "行: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("课程导入失败", e);
            return Result.error("文件解析失败: " + e.getMessage());
        }

        return Result.success(Map.of("success", success, "fail", fail, "total", success + fail, "errors", errors));
    }

    private String getString(Map<String, Object> row, String... keys) {
        for (String key : keys) {
            Object val = row.get(key);
            if (val != null) return val.toString().trim();
        }
        return "";
    }
}
