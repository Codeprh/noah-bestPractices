package org.geekbang.time.commonmistakes.redundantcode.reflection.right;

import lombok.Data;

/**
 * 要实现接口逻辑和逻辑实现的剥离，首先需要以 POJO 类(只有属性没有任何业务逻辑的 数据类)的方式定义所有的接口参数
 * 创建用户API
 */
@BankAPI(url = "/bank/createUser", desc = "创建用户接口")
@Data
public class CreateUserAPI extends AbstractAPI {
    @BankAPIField(order = 1, type = "S", length = 10)
    private String name;
    @BankAPIField(order = 2, type = "S", length = 18)
    private String identity;
    @BankAPIField(order = 4, type = "S", length = 11)
    private String mobile;
    @BankAPIField(order = 3, type = "N", length = 5)
    private int age;
}
