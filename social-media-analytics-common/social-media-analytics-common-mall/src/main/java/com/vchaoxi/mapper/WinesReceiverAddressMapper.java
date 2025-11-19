package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.WinesReceiverAddress;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
public interface WinesReceiverAddressMapper extends BaseMapper<WinesReceiverAddress> {

    /**
     * 根据用户查询用户收寄点地址拼接字符串
     *
     * @param userId
     * @return
     */

    List<String> selectStrByUserId(@Param("userId") Integer userId);

}
