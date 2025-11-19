package com.vchaoxi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户相关 - 用户表
 * </p>
 *
 * @author wangjiangtao
 * @since 2024-01-06
 */
@TableName("vc_user_user")
public class VcUserUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户所属公众号appid
     */
    private String mpAppid;

    /**
     * 用户公众号openid
     */
    private String mpOpenid;

    /**
     * 小程序appId
     */
    private String maAppid;

    /**
     * 小程序openId
     */
    private String maOpenid;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户网页应用openid
     */
    private String openid;

    /**
     * 用户unionid
     */
    private String unionid;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 普通用户性别  1为男性，2为女性
     */
    private Integer sex;

    /**
     * 普通用户个人资料填写的省份
     */
    private String province;

    /**
     * 普通用户个人资料填写的城市
     */
    private String city;

    /**
     * 国家，如中国为CN
     */
    private String country;

    /**
     * 用户头像
     */
    private String headimgUrl;

    /**
     * 积分
     */
    private Integer integral;

    /**
     * 会员等级（1:普通用户 2:会员 3:VIP，酒品售卖中使用）, 4, 免运费
     */
    private Integer memGrade;

    /**
     * 是否有获取邀请的三级用户的积分权限（0:无，1:有） 酒品售卖中使用
     */
    private Integer thirdPointsAccess;

    /**
     * 添加时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime insertTime;

    /**
     * 最后修改时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDelete;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    private String loginId;

    @ApiModelProperty("邀请人的登录id")
    private String inviteLoginId;

    @ApiModelProperty("邀请时间")
    private Date inviteTime;


    @ApiModelProperty("会员过期时间")
    private Date memberExpireDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getMpAppid() {
        return mpAppid;
    }

    public void setMpAppid(String mpAppid) {
        this.mpAppid = mpAppid;
    }
    public String getMpOpenid() {
        return mpOpenid;
    }

    public void setMpOpenid(String mpOpenid) {
        this.mpOpenid = mpOpenid;
    }
    public String getMaAppid() {
        return maAppid;
    }

    public void setMaAppid(String maAppid) {
        this.maAppid = maAppid;
    }
    public String getMaOpenid() {
        return maOpenid;
    }

    public void setMaOpenid(String maOpenid) {
        this.maOpenid = maOpenid;
    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getHeadimgUrl() {
        return headimgUrl;
    }

    public void setHeadimgUrl(String headimgUrl) {
        this.headimgUrl = headimgUrl;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getMemGrade() {
        return memGrade;
    }

    public void setMemGrade(Integer memGrade) {
        this.memGrade = memGrade;
    }
    public Integer getThirdPointsAccess() {
        return thirdPointsAccess;
    }

    public void setThirdPointsAccess(Integer thirdPointsAccess) {
        this.thirdPointsAccess = thirdPointsAccess;
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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getInviteLoginId() {
        return inviteLoginId;
    }

    public void setInviteLoginId(String inviteLoginId) {
        this.inviteLoginId = inviteLoginId;
    }

    public Date getInviteTime() {
        return inviteTime;
    }

    public void setInviteTime(Date inviteTime) {
        this.inviteTime = inviteTime;
    }

    public Date getMemberExpireDate() {
        return memberExpireDate;
    }

    public void setMemberExpireDate(Date memberExpireDate) {
        this.memberExpireDate = memberExpireDate;
    }

    @Override
    public String toString() {
        return "VcUserUser{" +
                "id=" + id +
                ", mpAppid='" + mpAppid + '\'' +
                ", mpOpenid='" + mpOpenid + '\'' +
                ", maAppid='" + maAppid + '\'' +
                ", maOpenid='" + maOpenid + '\'' +
                ", phone='" + phone + '\'' +
                ", openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", headimgUrl='" + headimgUrl + '\'' +
                ", integral=" + integral +
                ", memGrade=" + memGrade +
                ", thirdPointsAccess=" + thirdPointsAccess +
                ", insertTime=" + insertTime +
                ", updateTime=" + updateTime +
                ", isDelete=" + isDelete +
                ", deleteTime=" + deleteTime +
                ", loginId='" + loginId + '\'' +
                ", inviteLoginId='" + inviteLoginId + '\'' +
                ", inviteTime=" + inviteTime +
                ", memberExpireDate=" + memberExpireDate +
                '}';
    }
}
