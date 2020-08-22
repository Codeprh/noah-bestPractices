package org.geekbang.time.commonmistakes.springpart2.aopfeign.noahfegin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 描述:
 * 服务提供方
 *
 * @author Noah
 * @create 2020-05-28 17:12
 */
@FeignClient(name = "noahClient")
public interface NoahClient {

    @GetMapping("/feignaop/server")
//    @Cached(name = "noahCache.", key = "#userId", expire = 3600)
    String api(@RequestParam("userId") long userId);
}
