package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vchaoxi.entity.WineUserRelation;
import com.vchaoxi.vo.WineUserRelationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒品销售用户关系表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
public interface WineUserRelationMapper extends BaseMapper<WineUserRelation> {
    public List<WineUserRelationVo> userRelectionSelect(Page<WineUserRelationVo> page,
                                                        @Param("phone") String phone,
                                                        @Param("nickname") String nickname,
                                                        @Param("parentUserId")Integer parentUserId,
                                                        @Param("childUserId")Integer childUserId);


    /**
     * 查询我的邀请
     * @param parentUserId
     * @return
     */
    public List<WineUserRelationVo> myInviteSelect(@Param("parentUserId")Integer parentUserId);

    /**
     * 根据用户id查询该用户累计邀请的人数
     * @param userId
     * @return
     */
    public Integer selectInviteNum(@Param("userId") Integer userId);
}
