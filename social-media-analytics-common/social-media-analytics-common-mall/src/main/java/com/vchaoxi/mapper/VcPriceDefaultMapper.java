package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.VcPriceDefault;
import com.vchaoxi.vo.VcPriceDefaultVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-17
 */
public interface VcPriceDefaultMapper extends BaseMapper<VcPriceDefault> {
    public List<VcPriceDefaultVo> priceLimitSelect(@Param("goodsId") Integer goodsId);
}
