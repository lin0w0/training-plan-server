package com.hfk.training.modules.plan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hfk.training.common.PageResult;
import com.hfk.training.common.Result;
import com.hfk.training.modules.plan.entity.PlanCourse;
import com.hfk.training.modules.plan.entity.PlanSnapshot;
import com.hfk.training.modules.plan.entity.TrainingPlan;
import com.hfk.training.modules.plan.mapper.PlanCourseMapper;
import com.hfk.training.modules.plan.mapper.PlanSnapshotMapper;
import com.hfk.training.modules.plan.service.PlanService;
import com.hfk.training.modules.system.entity.Major;
import com.hfk.training.modules.system.entity.Student;
import com.hfk.training.modules.system.entity.SysUser;
import com.hfk.training.modules.system.mapper.MajorMapper;
import com.hfk.training.modules.system.mapper.StudentMapper;
import com.hfk.training.modules.system.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Tag(name = "培养计划管理", description = "培养计划CRUD、课程安排")
public class PlanController {

    private final PlanService planService;
    private final PlanCourseMapper planCourseMapper;
    private final PlanSnapshotMapper snapshotMapper;
    private final SysUserMapper sysUserMapper;
    private final StudentMapper studentMapper;
    private final MajorMapper majorMapper;

    // ============ 培养计划 CRUD ============
    @GetMapping("/page")
    @Operation(summary = "分页查询培养计划（按角色过滤）")
    public Result<PageResult<TrainingPlan>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String planName,
            @RequestParam(required = false) String status) {

        // 获取当前用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        LambdaQueryWrapper<TrainingPlan> w = new LambdaQueryWrapper<>();
        if (user != null && "teacher".equals(user.getUserType())) {
            // 教师：只看本学院已发布计划
            w.eq(TrainingPlan::getStatus, "PUBLISHED");
            List<Long> majorIds = majorMapper.selectList(new LambdaQueryWrapper<Major>().eq(Major::getCollegeId, user.getCollegeId()))
                    .stream().map(Major::getId).toList();
            w.in(majorIds.size() > 0, TrainingPlan::getMajorId, majorIds);
        } else if (user != null && "student".equals(user.getUserType())) {
            // 学生：只看本专业已发布计划
            w.eq(TrainingPlan::getStatus, "PUBLISHED");
            Student student = studentMapper.selectOne(new LambdaQueryWrapper<Student>().eq(Student::getUserId, user.getId()));
            if (student != null) w.eq(TrainingPlan::getMajorId, student.getMajorId());
        }
        // 管理员：看全部（不过滤）

        w.like(planName != null && !planName.isEmpty(), TrainingPlan::getPlanName, planName);
        if (status != null && !status.isEmpty()) {
            // 非管理员不能查看未发布计划（双重保护）
            if (user != null && !"admin".equals(user.getUserType()) && !"PUBLISHED".equals(status)) {
                return Result.success(PageResult.of(page, pageSize, 0, List.of()));
            }
            w.eq(TrainingPlan::getStatus, status);
        }
        w.orderByDesc(TrainingPlan::getCreateTime);
        Page<TrainingPlan> result = planService.page(new Page<>(page, pageSize), w);
        return Result.success(PageResult.of(page, pageSize, result.getTotal(), result.getRecords()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取计划详情")
    public Result<TrainingPlan> getById(@PathVariable Long id) {
        return Result.success(planService.getById(id));
    }

    @PostMapping
    @Operation(summary = "创建培养计划")
    @PreAuthorize("hasAnyAuthority('plan:add','ROLE_ADMIN')")
    public Result<Void> create(@RequestBody TrainingPlan plan) {
        Long count = planService.lambdaQuery().eq(TrainingPlan::getPlanCode, plan.getPlanCode()).count();
        if (count > 0) return Result.badRequest("计划编号已存在");
        planService.save(plan);
        return Result.ok("创建成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新培养计划")
    @PreAuthorize("hasAnyAuthority('plan:edit','ROLE_ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody TrainingPlan plan) {
        Long count = planService.lambdaQuery().eq(TrainingPlan::getPlanCode, plan.getPlanCode()).ne(TrainingPlan::getId, id).count();
        if (count > 0) return Result.badRequest("计划编号已存在");
        plan.setId(id);
        planService.updateById(plan);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除培养计划")
    @PreAuthorize("hasAnyAuthority('plan:delete','ROLE_ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        planService.removeById(id);
        return Result.ok("删除成功");
    }

    @PostMapping("/{id}/copy")
    @Operation(summary = "复制培养计划")
    @PreAuthorize("hasAnyAuthority('plan:copy','ROLE_ADMIN')")
    public Result<Void> copy(@PathVariable Long id) {
        planService.copyPlan(id);
        return Result.ok("复制成功");
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "发布计划（直接生效）")
    @PreAuthorize("hasAnyAuthority('plan:publish','ROLE_ADMIN')")
    public Result<Void> publish(@PathVariable Long id) {
        planService.publishPlan(id);
        return Result.ok("发布成功");
    }

    // ============ 计划课程 ============
    @GetMapping("/{id}/courses")
    @Operation(summary = "获取计划课程列表")
    public Result<List<PlanCourse>> getCourses(@PathVariable Long id) {
        return Result.success(planService.getVersionCourses(id));
    }

    @PutMapping("/{id}/courses")
    @Operation(summary = "保存计划课程安排")
    public Result<Void> saveCourses(@PathVariable Long id, @RequestBody List<PlanCourse> courses) {
        planService.saveVersionCourses(id, courses);
        return Result.ok("课程安排保存成功");
    }

    @GetMapping("/{id}/courses-detail")
    @Operation(summary = "获取计划课程详情（含课程名称、学分、分类）")
    public Result<Map<String, Object>> getCoursesDetail(@PathVariable Long id) {
        List<Map<String, Object>> courses = planCourseMapper.findCoursesWithDetail(id);

        // 按学期分组
        Map<Integer, List<Map<String, Object>>> bySemester = new LinkedHashMap<>();
        for (int s = 1; s <= 8; s++) bySemester.put(s, new ArrayList<>());
        for (Map<String, Object> c : courses) {
            Integer sem = (Integer) c.getOrDefault("semester", 1);
            bySemester.computeIfAbsent(sem, k -> new ArrayList<>()).add(c);
        }

        // 学分统计
        Map<String, Object> creditStats = new LinkedHashMap<>();
        int totalRequired = 0, totalElective = 0;
        Map<String, Integer> byCategory = new LinkedHashMap<>();
        for (Map<String, Object> c : courses) {
            Object creditObj = c.get("credit");
            double credit = creditObj != null ? ((Number) creditObj).doubleValue() : 0;
            Integer isRequired = (Integer) c.getOrDefault("is_required", 1);
            if (isRequired == 1) totalRequired += credit;
            else totalElective += credit;
            String cat = (String) c.getOrDefault("course_category", "OTHER");
            byCategory.merge(cat, (int) Math.round(credit), Integer::sum);
        }
        creditStats.put("totalRequired", totalRequired);
        creditStats.put("totalElective", totalElective);
        creditStats.put("totalCredits", totalRequired + totalElective);
        creditStats.put("byCategory", byCategory);

        TrainingPlan plan = planService.getById(id);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("plan", plan);
        result.put("semesters", bySemester);
        result.put("creditStats", creditStats);
        result.put("allCourses", courses);
        return Result.success(result);
    }

    // ============ 版本快照 ============
    @PostMapping("/{id}/snapshot")
    @Operation(summary = "保存当前课程为版本快照")
    public Result<Void> saveSnapshot(@PathVariable Long id, @RequestBody Map<String, String> body) {
        List<Map<String, Object>> courses = planCourseMapper.findCoursesWithDetail(id);
        PlanSnapshot snapshot = new PlanSnapshot();
        snapshot.setPlanId(id);
        snapshot.setVersionName(body.getOrDefault("versionName", "v" + System.currentTimeMillis()));
        snapshot.setChangeLog(body.getOrDefault("changeLog", ""));
        snapshot.setSnapshotData(cn.hutool.json.JSONUtil.toJsonStr(courses));
        snapshotMapper.insert(snapshot);
        return Result.ok("版本保存成功");
    }

    @GetMapping("/{id}/snapshots")
    @Operation(summary = "获取计划的所有版本快照")
    public Result<List<PlanSnapshot>> getSnapshots(@PathVariable Long id) {
        return Result.success(snapshotMapper.findByPlanId(id));
    }

    @GetMapping("/snapshot/{snapshotId}")
    @Operation(summary = "获取快照详情（课程列表）")
    public Result<Object> getSnapshotDetail(@PathVariable Long snapshotId) {
        PlanSnapshot s = snapshotMapper.selectById(snapshotId);
        if (s == null) return Result.error("快照不存在");
        try {
            return Result.success(cn.hutool.json.JSONUtil.parseArray(s.getSnapshotData()));
        } catch (Exception e) {
            return Result.error("解析失败");
        }
    }
}
