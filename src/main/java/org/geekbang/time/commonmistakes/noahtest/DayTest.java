package org.geekbang.time.commonmistakes.noahtest;

/**
 * 描述:
 * 日常测试
 *
 * @author Noah
 * @create 2020-05-09 10:58
 */
public class DayTest {

    public static void main(String[] args) {
        String test = "！！7987979发生发生312321不需要甜酸酱31231发送到发大水";

        String val = "不需要甜酸酱";
        if (test.indexOf(val) > -1) {
            System.out.println("匹配中了，不需要该配料");
        }
    }
}
