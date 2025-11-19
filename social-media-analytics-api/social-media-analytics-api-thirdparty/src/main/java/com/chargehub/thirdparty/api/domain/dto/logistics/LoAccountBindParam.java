package com.chargehub.thirdparty.api.domain.dto.logistics;

import lombok.Data;

/**
 * @Author：xiaobaopiqi
 * @Date：2024/1/30 13:46
 * @Project：vc-shoes-server
 * @Package：com.vchaoxi.wx.open.param.logistics
 * @Filename：LoAccountBindParam
 */
@Data
public class LoAccountBindParam {

    /**
     * 必填 ： 是
     * bind表示绑定，unbind表示解除绑定
     */
    private String type;

    /**
     * 必填 ： 是
     * 快递公司客户编码
     */
    private String biz_id;

    /**
     * 必填 ： 是
     * 快递公司ID
     */
    private String delivery_id;

    /**
     * 必填 ： 否
     * 快递公司客户密码
     */
    private String password;

    /**
     * 必填 ： 否
     * 备注内容（提交EMS审核需要） 格式要求： 电话：xxxxx 联系人：xxxxx 服务类型：xxxxx 发货地址：xxxx
     */
    private String remark_content;
}
