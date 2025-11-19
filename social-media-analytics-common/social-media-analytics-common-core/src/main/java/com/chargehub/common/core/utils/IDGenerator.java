package com.chargehub.common.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("all")
public class IDGenerator {

    private static final byte[] I6 = {121, 121, 121, 121, 77, 77, 100, 100, 72, 72, 109, 109, 115, 115, 83, 83, 83};


    private static final byte[] I2 = {77, 81};
    private static final byte[] I1 = {48};
    private static final byte[] I0 = new byte[0];
    public static final int idlen = 32;
    static Random random = new Random(System.currentTimeMillis());
    static AtomicLong step = new AtomicLong(0L);

    public static synchronized String newGUID() {
        String V0 = nowStrYYYYMMddHHmmssSSS();
        String V1 = Math.abs(random.nextLong()) + new String(I0);
        String V2 = V0 + V1;
        int V3 = 32 - V2.length();
        for (int V4 = 0; V4 < V3; V4++) {
            V2 = V2 + new String(I1);
        }
        if (V2.length() > 32) {
            V2 = V2.substring(0, 32);
        }
        return V2;
    }

    public static synchronized String newGUID(String V5) {
        String V0 = nowStrYYYYMMddHHmmssSSS();
        if (step.longValue() >= 10000000000000L) {
            step.set(0L);
        }
        String V1 = Math.abs(step.incrementAndGet()) + new String(I0);
        int V3 = 32 - V0.length() - V1.length() - V5.length();
        String V2 = new String(I0);
        for (int V4 = 0; V4 < V3; V4++) {
            V2 = V2 + new String(I1);
        }
        return V5 + V0 + V2 + V1;
    }

    public static synchronized String newNo(String V5) {
        String V6 = newGUID();
        String V7 = V5 + V6;
        return V7.substring(0, 32);
    }

    public static void main(String[] V8) {
        String V9 = newGUID();
        String V10 = newNo(new String(I2));
        System.out.println(V9);
        System.out.println(V9.length());
        System.out.println(V10);
        System.out.println(V10.length());
    }


    public static String nowStrYYYYMMddHHmmssSSS() {
        SimpleDateFormat V20 = new SimpleDateFormat(new String(I6));
        return V20.format(new Date());
    }
}
