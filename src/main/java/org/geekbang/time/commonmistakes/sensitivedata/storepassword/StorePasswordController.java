package org.geekbang.time.commonmistakes.sensitivedata.storepassword;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("storepassword")
public class StorePasswordController {

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;

    /**
     * 错误实现：无盐的md5加密
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong1")
    public UserData wrong1(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setPassword(DigestUtils.md5Hex(password));
        return userRepository.save(userData);
    }

    /**
     * 错误实现：盐值不够长度和随机
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong2")
    public UserData wrong2(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setPassword(DigestUtils.md5Hex("salt" + password));
        return userRepository.save(userData);
    }

    /**
     * 错误实现：盐值跟用户名相关，有专门的彩虹表
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong3")
    public UserData wrong3(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setPassword(DigestUtils.md5Hex(name + password));
        return userRepository.save(userData);
    }

    /**
     * 错误实现：两次的md5加密也秒破解
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("wrong4")
    public UserData wrong4(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setPassword(DigestUtils.md5Hex(DigestUtils.md5Hex(password)));
        return userRepository.save(userData);
    }

    /**
     * 最佳实践：盐值要随机、并且长度要超过20位，每个用户单独一个盐值：可明文存储
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("right")
    public UserData right(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setSalt(UUID.randomUUID().toString());
        userData.setPassword(DigestUtils.md5Hex(userData.getSalt() + password));
        return userRepository.save(userData);
    }

    /**
     * BCryptPasswordEncoder 生成的密码哈希的规律:
     *
     * @param name
     * @param password
     * @return
     */
    @GetMapping("better")
    public UserData better(@RequestParam(value = "name", defaultValue = "noah") String name, @RequestParam(value = "password", defaultValue = "Abcd1234") String password) {
        UserData userData = new UserData();
        userData.setId(1L);
        userData.setName(name);
        userData.setPassword(passwordEncoder.encode(password));
        userRepository.save(userData);
        log.info("match ? {}", passwordEncoder.matches(password, userData.getPassword()));
        return userData;
    }

    @GetMapping("performance")
    public void performance() {
        StopWatch stopWatch = new StopWatch();
        String password = "Abcd1234";
        stopWatch.start("MD5");
        DigestUtils.md5Hex(password);
        stopWatch.stop();
        stopWatch.start("BCrypt(10)");
        String hash1 = BCrypt.gensalt(10);
        BCrypt.hashpw(password, hash1);
        System.out.println(hash1);
        stopWatch.stop();
        stopWatch.start("BCrypt(12)");
        String hash2 = BCrypt.gensalt(12);
        BCrypt.hashpw(password, hash2);
        System.out.println(hash2);
        stopWatch.stop();
        stopWatch.start("BCrypt(14)");
        String hash3 = BCrypt.gensalt(14);
        BCrypt.hashpw(password, hash3);
        System.out.println(hash3);
        stopWatch.stop();
        log.info("{}", stopWatch.prettyPrint());
    }

}
