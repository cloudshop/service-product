package com.eyun.product.service;

import com.eyun.product.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@AuthorizedFeignClient(name = "user",decode404 = true)
public interface FeignMercurieClient {
    @PostMapping("/api/user-annexes-findUserByIdMercuryId")
    public Map<String,String> findUserMercuryId();
}
