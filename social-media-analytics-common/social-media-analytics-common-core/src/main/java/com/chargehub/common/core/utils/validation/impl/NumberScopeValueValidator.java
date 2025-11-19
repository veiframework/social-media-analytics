package com.chargehub.common.core.utils.validation.impl;

import com.chargehub.common.core.utils.validation.customized.NumberScopeValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class NumberScopeValueValidator implements ConstraintValidator<NumberScopeValue, Object> {

    private double minValue;
    private double maxValue;

    @Override
    public void initialize(NumberScopeValue numberScopeValue) {
        minValue = numberScopeValue.minValue();
        maxValue = numberScopeValue.maxValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value instanceof BigDecimal){
            if(((BigDecimal) value).compareTo(BigDecimal.valueOf(minValue)) >= 0  &&
                    ((BigDecimal) value).compareTo(BigDecimal.valueOf(maxValue)) <= 0){
                return true;
            }

        }
        return false;
    }
}
