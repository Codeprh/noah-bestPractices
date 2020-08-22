package org.geekbang.time.commonmistakes.advancedfeatures.reflectionissue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectionIssueApplication {

    public static void main(String[] args) throws Exception {

        ReflectionIssueApplication application = new ReflectionIssueApplication();
        application.wrong();
        application.right();

    }

    private void age(int age) {
        log.info("int age = {}", age);
    }

    private void age(Integer age) {
        log.info("Integer age = {}", age);
    }

    /**
     * 走int基本数据类型的重载方法
     *
     * @throws Exception
     */
    public void wrong() throws Exception {
        getClass().getDeclaredMethod("age", Integer.TYPE).invoke(this, Integer.valueOf("36"));
    }

    /**
     * 都是走Integer包装类的重载方法
     *
     * @throws Exception
     */
    public void right() throws Exception {
        getClass().getDeclaredMethod("age", Integer.class).invoke(this, Integer.valueOf("36"));
        getClass().getDeclaredMethod("age", Integer.class).invoke(this, 36);
    }
}
