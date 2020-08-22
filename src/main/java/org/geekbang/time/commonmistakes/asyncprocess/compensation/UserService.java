package org.geekbang.time.commonmistakes.asyncprocess.compensation;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 注册用户业务
 */
@Service
public class UserService {

    /**
     * 已经注册的用户列表
     */
    private List<User> users = new ArrayList<>();

    /**
     * 注册用户
     *
     * @return
     */
    public User register() {
        User user = new User();
        users.add(user);
        return user;
    }

    /**
     * 获取指定数量的用户id
     * @param id
     * @param limit
     * @return
     */
    public List<User> getUsersAfterIdWithLimit(long id, int limit) {
        return users.stream()
                .filter(user -> user.getId() >= id)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
