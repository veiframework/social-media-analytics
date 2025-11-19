package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.VcUserUser;
import com.vchaoxi.vo.UserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户相关 - 用户表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
public interface VcUserUserMapper extends BaseMapper<VcUserUser> {
    /**
     * 查询用户列表
     * @param page
     * @param phone
     * @param nickname
     * @return
     */
    public List<UserVo> userSelect(Page<UserVo> page, @Param("phone")String phone,
                                   @Param("nickname")String nickname,
                                   @Param(value = "memGrade") Integer memGrade,
                                   @Param("thirdPointsAccess") Integer thirdPointsAccess);

    public UserVo userSelectById(@Param("userId")Integer userId);
}
