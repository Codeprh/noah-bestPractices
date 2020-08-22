package org.geekbang.time.commonmistakes.springpart2.aopfeign;

import lombok.extern.slf4j.Slf4j;
import org.geekbang.time.commonmistakes.springpart2.aopfeign.feign.Client;
import org.geekbang.time.commonmistakes.springpart2.aopfeign.feign.ClientWithUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("feignaop")
@RestController
public class FeignAopConntroller {

    @Autowired
    private Client client;

    @Autowired
    private ClientWithUrl clientWithUrl;

    /**
     * 模拟调用feign请求
     *
     * @return
     */
    @GetMapping("client")
    public String client() {
        return client.api();
    }

    /**
     * 模拟feign调用第二版
     *
     * @return
     */
    @GetMapping("clientWithUrl")
    public String clientWithUrl() {
        return clientWithUrl.api();
    }

    /**
     * feign调用提供方
     *
     * @return
     */
    @GetMapping("server")
    public String server() {
        return "OK";
    }
}
