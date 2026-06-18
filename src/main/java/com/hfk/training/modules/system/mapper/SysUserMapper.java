package com.hfk.training.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hfk.training.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
