package com.chargehub.common.core.enums;

import java.util.Objects;

/**
 * 充电订单枚举
 * date: 2023/08
 *
 * @author TiAmo(13721682347 @ 163.com)
 */
public enum ChargeOrderLinkTypeEnum {

	/**
	 *
	 */
	SELF_EV(0, "自营订单"),

	EXTERNAL_EV(1, "启动第三方（互连互通）"),

	EXTERNAL_CALL_EV(2, "第三方启动自营（互连互通）"),


	;

	ChargeOrderLinkTypeEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	private final Integer code;

	private final String desc;

	public static ChargeOrderLinkTypeEnum getType(Integer code) {
		for (ChargeOrderLinkTypeEnum type : values()) {
			if (Objects.equals(type.code, code)) {
				return type;
			}
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}
