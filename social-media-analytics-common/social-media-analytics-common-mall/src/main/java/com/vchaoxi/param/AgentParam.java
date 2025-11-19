package com.vchaoxi.param;

import com.chargehub.common.core.utils.validation.customized.EnumValue;
import com.vchaoxi.entity.VcAgentWx;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class AgentParam {


    @NotNull(groups = {Edit.class,Del.class,Custom.class,SetOrderConf.class,EditMpInfo.class})
    private Integer id;


    /**
     * 商铺信息
     */
    @NotEmpty(groups = {Add.class,Edit.class,EditShopInfo.class},message = "客户名称不能为空")
    private String name;
    @NotEmpty(groups = {EditShopInfo.class},message = "logo不能为空")
    private String logo;
    @NotEmpty(groups = {EditShopInfo.class},message = "品牌不能为空")
    private String brand;
    private String qrCode;
    private String servicePhone;


    /**
     * 公众号商户信息
     */
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpAppId; //公众号appId
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpMainBody;  //公众号主体
    @NotEmpty(groups = {EditMpInfo.class})
    private String mpName;   //公众号名称
    private String mpQr;    //公众号二维码



    @NotEmpty(groups = {EditMchInfo.class})
    private String mchId;   //商户号
    @NotEmpty(groups = {EditMchInfo.class})
    private String mchKey;   //商户号秘钥
    @NotEmpty(groups = {EditMchInfo.class})
    private String mchCertName;   //商户号安全证书名称



    private Integer campusEdition;



    /**
     * 超管信息
     */
    private Integer adminId;
    @NotEmpty(groups = {Add.class,Edit.class})
    private String adminName;
    @NotEmpty(groups = {Add.class,Edit.class})
    private String adminMobile;
    private String adminPassword;


    @EnumValue(intValues = {0,1},groups = {Custom.class})
    private Integer custom;


    /**
     * 默认的公众号配置信息
     */
    private VcAgentWx defaultVcAgentWx;


    public interface Add {}
    public interface Edit {}
    public interface Del {}
    public interface Custom {}
    public interface EditShopInfo {}
    public interface EditMpInfo {}
    public interface EditMchInfo {}
    public interface SetOrderConf {}
}
