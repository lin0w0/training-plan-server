package com.hfk.training.modules.plan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("plan_snapshot")
public class PlanSnapshot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private String versionName;
    private String snapshotData;
    private String changeLog;
    private LocalDateTime createTime;
}
