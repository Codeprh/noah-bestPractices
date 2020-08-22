package org.geekbang.time.commonmistakes.springpart2.aopfeign.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 通过nginx负载均衡，替换ribbon负载均衡
 */
@FeignClient(name = "anotherClient", url = "http://localhost:45678")
public interface ClientWithUrl {
    @GetMapping("/feignaop/server")
    String api();
}
