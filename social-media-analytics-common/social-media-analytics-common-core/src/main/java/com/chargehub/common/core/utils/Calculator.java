package com.chargehub.common.core.utils;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * @author Zhanghaowei
 * @date 2024/12/07 15:12
 */
@Slf4j
public class Calculator<T> {

    private BigDecimal result;

    private final T object;

    private int scale = 4;

    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    private Function<T, ?> fieldFunc;

    private Expression expression;


    private Calculator(T object) {
        this.object = object;
    }

    public static <T> Calculator<T> create(T object) {
        return new Calculator<>(object);
    }

    public Calculator<T> scale(int scale) {
        this.scale = scale;
        return this;
    }

    public Calculator<T> roundingMode(RoundingMode roundingMode) {
        this.roundingMode = roundingMode;
        return this;
    }

    public Calculator<T> get(Function<T, ?> field) {
        if (object == null) {
            this.result = BigDecimal.ZERO;
            return this;
        }
        if (this.fieldFunc != null && this.expression != null) {
            BigDecimal prevNumber;
            if (result == null) {
                prevNumber = fieldFunc.andThen(v -> v == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(v))).apply(object);
            } else {
                prevNumber = result;
            }
            BigDecimal nextNumber = field.andThen(v -> v == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(v))).apply(object);
            this.result = this.expression.getFormula().calculate(prevNumber, nextNumber, new MathContext(scale, roundingMode));
        } else {
            this.fieldFunc = field;
        }
        return this;
    }


    public Calculator<T> add() {
        this.expression = Expression.ADD;
        return this;
    }

    public Calculator<T> sub() {
        this.expression = Expression.SUB;
        return this;
    }

    public Calculator<T> mul() {
        this.expression = Expression.MUL;
        return this;
    }

    public Calculator<T> div() {
        this.expression = Expression.DIV;
        return this;
    }

    public BigDecimal result() {
        return this.result;
    }

    public BigDecimal percent() {
        return this.result.multiply(new BigDecimal("100"));
    }


    public enum Expression {
        /**
         * 加
         */
        ADD(BigDecimal::add),

        /**
         * 减
         */
        SUB(BigDecimal::subtract),

        /**
         * 乘
         */
        MUL(BigDecimal::multiply),

        /**
         * 除
         */
        DIV((bigDecimal, divisor, mc) -> {
            try {
                return bigDecimal.divide(divisor, mc);
            } catch (ArithmeticException e) {
                log.warn(" [统计] 计算错误: " + e.getMessage());
                return BigDecimal.ZERO;
            }
        });


        private final Formula formula;

        Expression(Formula formula) {
            this.formula = formula;
        }

        public Formula getFormula() {
            return formula;
        }

        public interface Formula {

            /**
             * 计算
             *
             * @param a
             * @param b
             * @param mathContext
             * @return
             */
            BigDecimal calculate(BigDecimal a, BigDecimal b, MathContext mathContext);

        }
    }


}
