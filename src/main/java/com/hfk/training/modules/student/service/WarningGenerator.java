package com.hfk.training.modules.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hfk.training.modules.student.entity.AcademicWarning;
import com.hfk.training.modules.student.entity.StudentCourseRecord;
import com.hfk.training.modules.student.mapper.AcademicWarningMapper;
import com.hfk.training.modules.student.mapper.StudentCourseRecordMapper;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学业预警自动生成器
 * 检测规则：
 * 1. 挂科预警：有课程成绩 < 60
 * 2. 学分不足：当前已获学分 / 应修学分 < 60%
 * 3. 毕业风险：高年级学生总学分不足
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WarningGenerator {

    private final StudentMapper studentMapper;
    private final StudentCourseRecordMapper recordMapper;
    private final AcademicWarningMapper warningMapper;

    @Transactional
    public Map<String, Integer> generateAll() {
        int totalWarnings = 0;
        int failCourseCount = 0;
        int creditLowCount = 0;
        int graduationRiskCount = 0;

        List<Student> students = studentMapper.selectList(null);
        for (Student student : students) {
            List<StudentCourseRecord> records = recordMapper.selectList(
                    new LambdaQueryWrapper<StudentCourseRecord>()
                            .eq(StudentCourseRecord::getStudentId, student.getId()));

            // 规则1: 挂科预警
            List<StudentCourseRecord> failedCourses = records.stream()
                    .filter(r -> r.getScore() != null && r.getScore().doubleValue() < 60 && r.getIsPass() != null && r.getIsPass() == 0)
                    .toList();
            if (!failedCourses.isEmpty()) {
                StringBuilder content = new StringBuilder("以下课程不及格: ");
                for (StudentCourseRecord fc : failedCourses) {
                    content.append(String.format("课程ID%s(%.1f分); ", fc.getCourseId(), fc.getScore()));
                }
                createWarning(student.getId(), "FAIL_COURSE", getFailLevel(failedCourses.size()), content.toString());
                failCourseCount++;
                totalWarnings++;
            }

            // 规则2: 学分不足预警 (已修学期数 * 20 为应修学分，实际已获学分为准)
            int passedCount = (int) records.stream().filter(r -> r.getIsPass() != null && r.getIsPass() == 1).count();
            int semestersStudied = records.stream().map(StudentCourseRecord::getSemester).collect(Collectors.toSet()).size();
            int expectedCredits = semestersStudied * 20; // 每学期约20学分
            if (semestersStudied >= 2 && passedCount * 3 < expectedCredits * 0.6) { // 粗略估算: 每门课约3学分
                int earnedCredits = passedCount * 3;
                createWarning(student.getId(), "CREDIT_LOW",
                        earnedCredits < expectedCredits * 0.4 ? "红色" : "橙色",
                        String.format("已获约%d学分，应修约%d学分，差距较大", earnedCredits, expectedCredits));
                creditLowCount++;
                totalWarnings++;
            }

            // 规则3: 毕业风险 (第6学期及以上，总学分 < 120)
            if (semestersStudied >= 6 && passedCount * 3 < 120) {
                createWarning(student.getId(), "GRADUATION_RISK", "橙色",
                        String.format("已修%d学期，预估已获约%d学分，离毕业要求160学分差距较大", semestersStudied, passedCount * 3));
                graduationRiskCount++;
                totalWarnings++;
            }
        }

        log.info("预警生成完成: 总{}条(挂科{}, 学分不足{}, 毕业风险{})",
                totalWarnings, failCourseCount, creditLowCount, graduationRiskCount);

        Map<String, Integer> result = new HashMap<>();
        result.put("total", totalWarnings);
        result.put("failCourse", failCourseCount);
        result.put("creditLow", creditLowCount);
        result.put("graduationRisk", graduationRiskCount);
        return result;
    }

    private void createWarning(Long studentId, String type, String level, String content) {
        // 避免重复生成
        Long exists = warningMapper.selectCount(new LambdaQueryWrapper<AcademicWarning>()
                .eq(AcademicWarning::getStudentId, studentId)
                .eq(AcademicWarning::getWarningType, type)
                .eq(AcademicWarning::getIsResolved, 0));
        if (exists > 0) return;

        AcademicWarning warning = new AcademicWarning();
        warning.setStudentId(studentId);
        warning.setWarningType(type);
        warning.setWarningLevel(level);
        warning.setWarningContent(content);
        warning.setIsResolved(0);
        warningMapper.insert(warning);
    }

    private String getFailLevel(int failCount) {
        if (failCount >= 4) return "红色";
        if (failCount >= 2) return "橙色";
        return "黄色";
    }
}
