package com.vchaoxi.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 代理商相关 - 微信配置表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_agent_wx")
public class AgentWxVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 商铺id
     */
    private Integer agentId;

    /**
     * 商家公众号appid
     */
    private String mpAppid;

    /**
     * 商家公众号appSecret
     */
    private String mpAppSecret;

    /**
     * 公众号token
     */
    private String mpToken;

    /**
     * 公众号aesKey
     */
    private String mpAesKey;

    /**
     * 商家公众号二维码
     */
    private String mpQr;

    /**
     * 商家公众号主体
     */
    private String mpMainBody;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户号秘钥
     */
    private String mchKey;

    /**
     * 商户号证书名称
     */
    private String mchCertName;

    /**
     * 小程序appId
     */
    private String maAppid;

    /**
     * 小程序秘钥
     */
    private String maSecret;

    /**
     * 小程序名称
     */
    private String maName;

    /**
     * 小程序二维码
     */
    private String maQr;

    /**
     * 公众号名称
     */
    private String mpName;

    /**
     * 是否允许商铺自定义公众号商户信息    1允许  0不允许
     */
    private Integer custom;

    /**
     * 添加时间
     */
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }
    public String getMpAppid() {
        return mpAppid;
    }

    public void setMpAppid(String mpAppid) {
        this.mpAppid = mpAppid;
    }
    public String getMpAppSecret() {
        return mpAppSecret;
    }

    public void setMpAppSecret(String mpAppSecret) {
        this.mpAppSecret = mpAppSecret;
    }
    public String getMpToken() {
        return mpToken;
    }

    public void setMpToken(String mpToken) {
        this.mpToken = mpToken;
    }
    public String getMpAesKey() {
        return mpAesKey;
    }

    public void setMpAesKey(String mpAesKey) {
        this.mpAesKey = mpAesKey;
    }
    public String getMpQr() {
        return mpQr;
    }

    public void setMpQr(String mpQr) {
        this.mpQr = mpQr;
    }
    public String getMpMainBody() {
        return mpMainBody;
    }

    public void setMpMainBody(String mpMainBody) {
        this.mpMainBody = mpMainBody;
    }
    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }
    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }
    public String getMchCertName() {
        return mchCertName;
    }

    public void setMchCertName(String mchCertName) {
        this.mchCertName = mchCertName;
    }
    public String getMaAppid() {
        return maAppid;
    }

    public void setMaAppid(String maAppid) {
        this.maAppid = maAppid;
    }
    public String getMaSecret() {
        return maSecret;
    }

    public void setMaSecret(String maSecret) {
        this.maSecret = maSecret;
    }
    public String getMaName() {
        return maName;
    }

    public void setMaName(String maName) {
        this.maName = maName;
    }
    public String getMaQr() {
        return maQr;
    }

    public void setMaQr(String maQr) {
        this.maQr = maQr;
    }
    public String getMpName() {
        return mpName;
    }

    public void setMpName(String mpName) {
        this.mpName = mpName;
    }
    public Integer getCustom() {
        return custom;
    }

    public void setCustom(Integer custom) {
        this.custom = custom;
    }
    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
    public LocalDateTime getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(LocalDateTime deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public String toString() {
        return "VcAgentWx{" +
            "id=" + id +
            ", agentId=" + agentId +
            ", mpAppid=" + mpAppid +
            ", mpAppSecret=" + mpAppSecret +
            ", mpToken=" + mpToken +
            ", mpAesKey=" + mpAesKey +
            ", mpQr=" + mpQr +
            ", mpMainBody=" + mpMainBody +
            ", mchId=" + mchId +
            ", mchKey=" + mchKey +
            ", mchCertName=" + mchCertName +
            ", maAppid=" + maAppid +
            ", maSecret=" + maSecret +
            ", maName=" + maName +
            ", maQr=" + maQr +
            ", mpName=" + mpName +
            ", custom=" + custom +
            ", insertTime=" + insertTime +
            ", updateTime=" + updateTime +
            ", isDelete=" + isDelete +
            ", deleteTime=" + deleteTime +
        "}";
    }
}
