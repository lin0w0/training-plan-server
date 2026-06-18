package com.hfk.training.modules.plan.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hfk.training.common.BusinessException;
import com.hfk.training.modules.plan.entity.PlanCourse;
import com.hfk.training.modules.plan.entity.TrainingPlan;
import com.hfk.training.modules.plan.mapper.PlanCourseMapper;
import com.hfk.training.modules.plan.mapper.TrainingPlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService extends ServiceImpl<TrainingPlanMapper, TrainingPlan> {

    private final PlanCourseMapper planCourseMapper;
    private final TrainingPlanMapper trainingPlanMapper;

    public Page<TrainingPlan> pagePlans(int page, int pageSize, String planName, String status) {
        LambdaQueryWrapper<TrainingPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(planName), TrainingPlan::getPlanName, planName)
                .eq(StringUtils.hasText(status), TrainingPlan::getStatus, status)
                .orderByDesc(TrainingPlan::getCreateTime);
        return page(new Page<>(page, pageSize), wrapper);
    }

    @Transactional
    public void copyPlan(Long id) {
        TrainingPlan source = getById(id);
        if (source == null) throw new BusinessException("原计划不存在");
        TrainingPlan copy = new TrainingPlan();
        copy.setPlanCode(source.getPlanCode() + "-COPY");
        copy.setPlanName(source.getPlanName() + "(副本)");
        copy.setMajorId(source.getMajorId());
        copy.setEnrollmentYearStart(source.getEnrollmentYearStart());
        copy.setEnrollmentYearEnd(source.getEnrollmentYearEnd());
        copy.setDuration(source.getDuration());
        copy.setTotalCredits(source.getTotalCredits());
        copy.setRequiredCredits(source.getRequiredCredits());
        copy.setElectiveCredits(source.getElectiveCredits());
        copy.setGeneralCredits(source.getGeneralCredits());
        copy.setCoreCredits(source.getCoreCredits());
        copy.setDescription(source.getDescription());
        copy.setStatus("PUBLISHED");
        save(copy);
    }

    /**
     * 直接发布计划
     */
    @Transactional
    public void publishPlan(Long planId) {
        TrainingPlan plan = getById(planId);
        if (plan == null) throw new BusinessException("计划不存在");
        plan.setStatus("PUBLISHED");
        updateById(plan);
    }

    // ====== 计划课程 ======
    public List<PlanCourse> getVersionCourses(Long planId) {
        LambdaQueryWrapper<PlanCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlanCourse::getPlanId, planId)
                .orderByAsc(PlanCourse::getSemester, PlanCourse::getSortOrder);
        return planCourseMapper.selectList(wrapper);
    }

    @Transactional
    public void saveVersionCourses(Long planId, List<PlanCourse> courses) {
        if (courses == null || courses.isEmpty()) return;
        Set<Integer> semesters = courses.stream().map(PlanCourse::getSemester).collect(Collectors.toSet());
        LambdaQueryWrapper<PlanCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlanCourse::getPlanId, planId)
                .in(PlanCourse::getSemester, semesters);
        planCourseMapper.delete(wrapper);
        for (PlanCourse pc : courses) {
            pc.setPlanId(planId);
            planCourseMapper.insert(pc);
        }
    }
}
