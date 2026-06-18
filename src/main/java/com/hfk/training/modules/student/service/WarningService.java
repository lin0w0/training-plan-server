package com.hfk.training.modules.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.modules.student.entity.AcademicWarning;
import com.hfk.training.modules.student.mapper.AcademicWarningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WarningService extends ServiceImpl<AcademicWarningMapper, AcademicWarning> {

    public Page<AcademicWarning> pageWarnings(int page, int pageSize, String warningLevel, Integer isResolved) {
        LambdaQueryWrapper<AcademicWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(warningLevel), AcademicWarning::getWarningLevel, warningLevel)
                .eq(isResolved != null, AcademicWarning::getIsResolved, isResolved)
                .orderByDesc(AcademicWarning::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    public Page<Map<String, Object>> pageWarningsWithNames(int page, int pageSize, String warningLevel, Integer isResolved, Long studentId) {
        return getBaseMapper().pageWithNames(new Page<>(page, pageSize), warningLevel, isResolved, studentId);
    }

    public List<AcademicWarning> getWarningsByStudentId(Long studentId) {
        return list(new LambdaQueryWrapper<AcademicWarning>()
                .eq(AcademicWarning::getStudentId, studentId)
                .orderByDesc(AcademicWarning::getCreateTime));
    }

    public void resolveWarning(Long id, String remark) {
        AcademicWarning warning = getById(id);
        if (warning != null) {
            warning.setIsResolved(1);
            warning.setResolveTime(LocalDateTime.now());
            warning.setResolveRemark(remark);
            updateById(warning);
        }
    }

    public void generateWarnings() {
        // TODO: 实现预警自动生成算法
        // 1. 查询所有学生成绩
        // 2. 检查不及格课程 → FAIL_COURSE 预警
        // 3. 检查学分是否达标 → CREDIT_LOW 预警
        // 4. 检查毕业要求 → GRADUATION_RISK 预警
    }
}
