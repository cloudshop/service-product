package com.eyun.product.service.impl;

import com.eyun.product.service.FeignOrderCilent;
import com.eyun.product.service.FeignShopCarClient;
import com.eyun.product.service.ProductSkuService;
import com.eyun.product.domain.ProductSku;
import com.eyun.product.repository.ProductSkuRepository;
import com.eyun.product.service.dto.ProductSkuDTO;
import com.eyun.product.service.mapper.ProductSkuMapper;
import com.eyun.product.web.rest.errors.BadRequestAlertException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service Implementation for managing ProductSku.
 */
@Service
@Transactional
public class ProductSkuServiceImpl implements ProductSkuService {

    private final Logger log = LoggerFactory.getLogger(ProductSkuServiceImpl.class);

    private final ProductSkuRepository productSkuRepository;

    private final ProductSkuMapper productSkuMapper;

    @Autowired
    FeignShopCarClient feignShopCarClient;
    @Autowired
    FeignOrderCilent feignOrderCilent;
    public ProductSkuServiceImpl(ProductSkuRepository productSkuRepository, ProductSkuMapper productSkuMapper) {
        this.productSkuRepository = productSkuRepository;
        this.productSkuMapper = productSkuMapper;
    }

    /**
     * Save a productSku.
     *
     * @param productSkuDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProductSkuDTO save(ProductSkuDTO productSkuDTO) {
        log.debug("Request to save ProductSku : {}", productSkuDTO);
        ProductSku productSku = productSkuMapper.toEntity(productSkuDTO);
        productSku = productSkuRepository.save(productSku);
        return productSkuMapper.toDto(productSku);
    }

    @Override
    public Map updateStockCount(ProductSkuDTO productSkuDTO, Integer processType) {
        String messgae="success";
        String content="更改库存成功";;
        ProductSku productSku=productSkuRepository.findOne(productSkuDTO.getId());
        if (productSku!=null){
            if (processType==0){//减库存
                if(productSku.getCount()>=productSkuDTO.getCount()){
                    productSku.setCount(productSku.getCount()-productSkuDTO.getCount());
                    productSkuRepository.save(productSku);
                }else {
                    messgae="failed";
                    content="更改库存失败！库存不足";
                }
            }else {//加库存
                productSku.setCount(productSku.getCount()+productSkuDTO.getCount());
                productSkuRepository.save(productSku);
            }
        }else {
            messgae="failed";
            content="更改库存失败！无此商品sku";
        }
        Map result=new HashMap();
        result.put("message",messgae);
        result.put("content",content);
        return result;
    }

    /*type：0 编辑 ，1：下架*/
    @Override
    public ProductSkuDTO skuHandle(Integer type,ProductSkuDTO productSkuDTO) throws Exception {
        ProductSku productSku=productSkuRepository.findOne(productSkuDTO.getId());
        switch (type){
            case 0:
                if (productSkuDTO.getCount()!=null&&productSkuDTO.getCount()>0){
                    productSku.setCount(productSkuDTO.getCount());
                }
                if (productSkuDTO.getPrice()!=null){
                    productSku.setPrice(productSkuDTO.getPrice());
                }
                if (productSkuDTO.getTransfer()!=null){
                    productSku.setTransfer(productSkuDTO.getTransfer());
                }
                break;
            case 1:
                Map<String,String> sku=feignShopCarClient.getShopCartBySkuId(productSkuDTO.getId());
                if (sku!=null){
                    throw new BadRequestAlertException("该商品已加入购物车，不能下架","shopcar","allreadyInshopcar");
                }
                List list=feignOrderCilent.findOrderItemByskuid(productSkuDTO.getId());
                if (!list.isEmpty()){
                    throw new BadRequestAlertException("该商品已结算，不能下架","order","allreadyInOrder");
                }
                productSku.status(1);//0：上架 1：下架
                break;
        }
        productSku=productSkuRepository.save(productSku);
        return productSkuMapper.toDto(productSku);
    }

    /**
     * Get all the productSkus.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSkuDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ProductSkus");
        return productSkuRepository.findAll(pageable)
            .map(productSkuMapper::toDto);
    }

    /**
     * Get one productSku by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProductSkuDTO findOne(Long id) {
        log.debug("Request to get ProductSku : {}", id);
        ProductSku productSku = productSkuRepository.findOne(id);
        return productSkuMapper.toDto(productSku);
    }

    /**
     * Delete the productSku by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProductSku : {}", id);
        productSkuRepository.delete(id);
    }
}
