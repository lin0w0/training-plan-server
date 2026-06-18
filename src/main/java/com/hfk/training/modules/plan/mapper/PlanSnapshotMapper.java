package com.hfk.training.modules.plan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.plan.entity.PlanSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlanSnapshotMapper extends BaseMapper<PlanSnapshot> {

    @Select("SELECT * FROM plan_snapshot WHERE plan_id = #{planId} ORDER BY create_time DESC")
    List<PlanSnapshot> findByPlanId(@Param("planId") Long planId);
}
