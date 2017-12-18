package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author: Ji YongGuang.
 * @date: 17:43 2017/12/14.
 */
public class BigDecimalTest {

    @Test
    public void testDouble() {
        Double d1 = new Double(0.05);
        Double d2 = new Double(0.01);
        System.out.println(d1 + d2);

        BigDecimal b1 = new BigDecimal("0.01");
        BigDecimal b2 = new BigDecimal("0.05");

        b1.add(b2);
    }
}
