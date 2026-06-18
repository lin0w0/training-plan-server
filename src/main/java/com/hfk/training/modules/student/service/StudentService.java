package com.hfk.training.modules.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.modules.student.entity.StudentCourseRecord;
import com.hfk.training.modules.student.mapper.StudentCourseRecordMapper;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.mapper.StudentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService extends ServiceImpl<StudentMapper, Student> {

    private final StudentCourseRecordMapper courseRecordMapper;

    public List<StudentCourseRecord> getStudentCourses(Long studentId) {
        return courseRecordMapper.selectList(
                new LambdaQueryWrapper<StudentCourseRecord>()
                        .eq(StudentCourseRecord::getStudentId, studentId)
                        .orderByAsc(StudentCourseRecord::getSemester));
    }

    public void addCourseRecord(StudentCourseRecord record) {
        courseRecordMapper.insert(record);
    }

    public Map<String, Object> getStudentProgress(Long studentId) {
        List<StudentCourseRecord> records = getStudentCourses(studentId);
        int totalEarnedCredits = records.stream()
                .filter(r -> r.getIsPass() != null && r.getIsPass() == 1)
                .mapToInt(r -> 0) // 实际应从course表关联查学分
                .sum();

        Map<String, Object> progress = new HashMap<>();
        progress.put("studentId", studentId);
        progress.put("totalCourses", records.size());
        progress.put("passedCourses", records.stream().filter(r -> r.getIsPass() != null && r.getIsPass() == 1).count());
        progress.put("failedCourses", records.stream().filter(r -> r.getIsPass() != null && r.getIsPass() == 0).count());
        progress.put("records", records);
        return progress;
    }

    public Map<String, Object> getStudentPlanView(Long studentId) {
        Map<String, Object> view = new HashMap<>();
        view.put("studentId", studentId);
        view.put("semesters", Collections.emptyList()); // TODO: 实现完整逻辑
        return view;
    }
}
