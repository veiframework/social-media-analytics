package com.chargehub.thirdparty.api.domain.dto.chwo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * ChWoRequest ch 外部工单请求体
 * date: 2024/12
 *
 * @author <a href="13721682347@163.com">TiAmo</a>
 */
@Data
public class ChEqtWoDTO {

    @ApiModelProperty(value = "1 工单WEB，2 新能源平台", example = "2")
    private String source = "2";

    @ApiModelProperty(value = "1 停车场，2 充电站", example = "2")
    private String addressType = "2";

    @ApiModelProperty(value = "新能源站点ID", example = "3")
    private String addressId;

    @ApiModelProperty(value = "固定3", example = "3")
    private String objectId = "3";

    @ApiModelProperty(value = "报修对象名称（此处固定传值：充电桩）", example = "充电桩")
    private String objectName = "充电桩";

    @ApiModelProperty(value = "地址", example = "河南省洛阳市")
    private String addressAdd = "";

    @ApiModelProperty(value = "描述信息", example = "设备离线")
    private String description = "";

    @ApiModelProperty("图片/视频，多个图片地址,隔开")
    private String img = "";

    @ApiModelProperty(value = "桩号",example = "471000230304302007")
    private String deviceNo = "";

    @ApiModelProperty(value = "关联订单Id", example = "0123456789")
    private String relatedOrderId = "";

}
