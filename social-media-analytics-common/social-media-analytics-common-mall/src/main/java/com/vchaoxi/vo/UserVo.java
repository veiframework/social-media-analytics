package com.vchaoxi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户相关 - 用户表
 * </p>
 *
 * @author wangjiangtao
 * @since 2022-09-05
 */
@Data
public class UserVo {

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
     * 会员等级（1:普通用户 2:会员 3:VIP，酒品售卖中使用）
     */
    private Integer memGrade;

    /**
     * 是否有获取邀请的三级用户的积分权限（0:无，1:有） 酒品售卖中使用
     */
    private Integer thirdPointsAccess;

    /**
     * 添加时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime insertTime;

    /**
     * 用户账号
     */
    private Integer userAccount;

    /**
     * 注册说明
     */
    private String signupDetails;

    /**
     * 升级说明
     */
    private String upgradeDetails;
    /**
     * 父类电话
     */
    private String parentUserPhone;

    @ApiModelProperty("邀请人的登录id")
    private String inviteLoginId;

    @ApiModelProperty("邀请时间")
    private Date inviteTime;


    @ApiModelProperty("会员过期时间")
    private Date memberExpireDate;
}
