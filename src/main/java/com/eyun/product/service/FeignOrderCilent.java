package com.eyun.product.service;

import com.eyun.product.client.AuthorizedFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@AuthorizedFeignClient(name = "order",decode404 = true)
public interface FeignOrderCilent {
    @GetMapping("/api/findOrderItemByskuid/{skuId}")
    public List findOrderItemByskuid(@PathVariable("skuId") Long skuId);
}
