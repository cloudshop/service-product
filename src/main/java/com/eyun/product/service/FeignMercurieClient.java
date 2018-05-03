package com.eyun.product.service;

import com.eyun.product.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Map;

@AuthorizedFeignClient(name = "user",decode404 = true)
public interface FeignMercurieClient {
    @GetMapping("/api/mercuries/getUserIdMercuryId")
    public Map findUserMercuryId();
}
