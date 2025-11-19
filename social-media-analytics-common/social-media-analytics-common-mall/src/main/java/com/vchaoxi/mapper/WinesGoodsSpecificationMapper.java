package com.vchaoxi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vchaoxi.entity.WinesGoodsSpecification;
import com.vchaoxi.vo.WinesGoodsSpecificationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 酒类售卖商品规格表 Mapper 接口
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-07-29
 */
public interface WinesGoodsSpecificationMapper extends BaseMapper<WinesGoodsSpecification> {
    public List<WinesGoodsSpecificationVo> goodsSpecificationList(@Param("goodsId") Integer goodsId);
}
