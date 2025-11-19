package com.chargehub.thirdparty.domain.dto.duofu;

import lombok.Data;

/**
 * @description: 多弗停车请求参数
 * @author: lfy
 * @create: 2024-07-29 11:06
 */
@Data
public class DuofuDiscountReqDTO {

    /**
     * ⻋牌号
     * 必填
     */
    private String plateNo;

    /**
     * 唯⼀⻋场标识 (⾏呗提供)
     * 必填
     */
    private String merchId;// Y String 唯⼀⻋场标识 (⾏呗提供)

    /**
     * 减免类型（1：⾦额[单位:分]，2：时⻓[单位:分钟]，3：折扣，4：全免 ）
     * 必填
     */
    private String type;// Y String 减免类型（1：⾦额[单位:分]，2：时⻓[单位:分钟]，3：折扣，4：全免 ）

    /**
     * 减免数值（和减免类型对应，减免类型为⾦额时，传⾦额值（单位：分）；时⻓时，传分钟数；折扣时，6折传600，95折传950；全免时，传0即可）
     * 必填
     */
    private String discount;// Y String 减免数值（和减免类型对应，减免类型为⾦额时，传⾦额值（单位：分）；时⻓时，传分钟数；折扣时，6折传600，95折传950；全免时，传0即可）

    /**
     * 减免流⽔号（相同流⽔号的请求会被去重，若需多次减免需更换流⽔号）BIZLD的⼩写
     * 必填
     */
    private String bizld;// Y String 减免流⽔号（相同流⽔号的请求会被去重，若需多次减免需更换流⽔号）BIZLD的⼩写

    /**
     * 签名 (签名秘钥由⾏呗平台颁发，调⽤⽅按照签名规则⽣成，停⻋场系统通过验签规则校验签名是否正确)
     * 必填
     */
    private String sign;// Y String 签名 (签名秘钥由⾏呗平台颁发，调⽤⽅按照签名规则⽣成，停⻋场系统通过验签规则校验签名是否正确)

    /**
     * ⻋牌颜⾊（蓝⾊：0000FF00 ，⻩⾊：FFFF0000，绿⾊：00FF0000) 如果不传，则7位的⻋牌默认⽤蓝⾊查场内，8位的⻋牌⽤绿⾊查场内
     * 选填
     */
    private String plateNoColor;

    /**
     * 减免名称（建议传充电品牌名称）
     * 必填
     */
    private String discountName;// Y String 减免名称（建议传充电品牌名称）

    /**
     * 去重类型 0默认为流⽔号 1为进场时间（若不传，则默认为流⽔号去重，只⽤流⽔号去重，若传1，则会⽤流⽔号+进场时间去重，即此次进场若已经有此接⼝的减免，则⽆法绑定减免）
     * 必填
     */
    private String filterType;// Y String 去重类型 0默认为流⽔号 1为进场时间（若不传，则默认为流⽔号去重，只⽤流⽔号去重，若传1，则会⽤流⽔号+进场时间去重，即此次进场若已经有此接⼝的减免，则⽆法绑定减免）

    /**
     * 是否询价 0否 1是（若不传，则默认为否，执⾏绑定减免。 若传是，则返回绑定优惠后的计费⾦额，但不绑定减免）
     * 选填
     */
    private String queryAmount;
}
