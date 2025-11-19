package com.chargehub.common.core.enums;

/**
 * 会员操作枚举
 *
 * @author 孟权伟
 * @since 2023/07/26
 **/
public enum MemberTagsEnum {
    /**
     * 会员注册
     */
    MEMBER_REGISTER("会员注册"),
    /**
	 * 会员临时注册
	 */
	MEMBER_TEMP_REGISTER("会员临时注册"),
	/**
	 * 支付宝退款
	 */
	ALIPAY_REFUND("支付宝退款"),
	/**
	 * 微信退款
	 */
	WALLET_REFUND("微信退款"),
	/**
	 * 微信退款
	 */
	TEMP_WALLET_REFUND("临时用户退款"),
	/**
	 * 支付宝月卡退款
	 */
	CARD_ALIPAY_REFUND("支付宝月卡退款"),
	/**
	 * 微信月卡退款
	 */
	CARD_WALLET_REFUND("微信月卡退款"),
    /**
     * 临时用户会员注册
     */
    TEMP_MEMBER_REGISTER("临时用户会员注册"),
    /**
     * 会员注册
     */
    MEMBER_LOGIN("会员登录"),
    /**
     * 会员签到
     */
    MEMBER_SING("会员签到"),
    /**
     * 会员提现
     */
    MEMBER_WITHDRAWAL("会员提现"),
    /**
     * 会员信息更改
     */
    MEMBER_INFO_EDIT("会员信息更改"),
    /**
     * 会员积分变动
     */
    MEMBER_POINT_CHANGE("会员积分变动"),
    /**
     * 会员使用联合登录
     */
    MEMBER_CONNECT_LOGIN("会员使用联合登录"),
	/**
	 * 支付宝拼单失败退款
	 */
	ALIPAY_COllORDER_REFUND("支付宝拼单失败退款"),
	/**
	 * 微信拼单失败退款
	 */
	WX_COllORDER_REFUND("微信拼单失败退款");

    private final String description;

    MemberTagsEnum(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }


}
