package com.eyun.product.service;

import com.eyun.product.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@AuthorizedFeignClient(name = "user",decode404 = true)
public interface FeignMercurieClient {
    @GetMapping("/api/mercuries/getUserIdMercuryId")
    public Map findUserMercuryId();

    @GetMapping("/api/mercuries/findbyname/{mercuriename}")
    public List<Map> findByNameLike(@PathVariable("mercuriename") String mercuriename);
}
