package com.eyun.product.service;

import com.eyun.product.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@AuthorizedFeignClient(name = "shoppingcart",decode404 = true)
public interface FeignShopCarClient {

    @GetMapping("/api/shoppingcar/{skuId}")
    public List<Map> getShopCartBySkuId(@PathVariable("skuId") Long skuId);
}
