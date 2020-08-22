package org.geekbang.time.commonmistakes.numeralcalculations.rounding;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 描述:
 * 精度问题
 *
 * @author Noah
 * @create 2020-04-22 07:07
 */
public class NoahApplication {

    public static void main(String[] args) {
//        double num1 = 3.35;
//        float num2 = 3.35f;
//        System.out.println(String.format("%.2f", num1));//四舍五入
//        System.out.println(String.format("%.2f", num2));


        double num1 = 3.35;
        float num2 = 3.35f;
        DecimalFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.HALF_UP);
        System.out.println(format.format(num1));
        format.setRoundingMode(RoundingMode.HALF_UP);
        System.out.println(format.format(num2));
    }
}
