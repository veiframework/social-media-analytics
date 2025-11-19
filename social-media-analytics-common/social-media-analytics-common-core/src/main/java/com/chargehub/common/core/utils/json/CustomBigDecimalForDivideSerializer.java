package com.chargehub.common.core.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 清分系统需要的金额格式转换
 */
public class CustomBigDecimalForDivideSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value!= null) {
            if (value.scale() > 0) {  //有小数位时
                if (value.stripTrailingZeros().scale() > 0) {  // 10.10 10.1 转换为 10.1
                    //BigDecimal strippedValue = value.stripTrailingZeros();
                    gen.writeString(value.stripTrailingZeros().toString());
                } else { // 10, 10.0, 10.00 转换为 10.0
                    gen.writeString(value.setScale(1).toString());
                }
            } if (value.scale() == 0){  // 整数无小数位, 0 转化为 0.0, 1 转为 1.0
                gen.writeString(value.setScale(1).toString());
            }
        } else {
            gen.writeNull();
        }
    }

}
