package com.eyun.product.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductContentDTO implements Serializable {
    @NotNull
    private String productName;
    @NotNull
    private String mainImage;//商品主图url
    @NotNull
    private Long categoryId;

    private Long brandId;


    private Long shopId;

    @NotNull
    private Integer listPrice;//列表价格
    private String description;//商品介绍
    private Integer paymentType;//支付方式
    private Long address;//商品所在地
    private Integer freight;//运费
    private List<Map<String,String>> attr=new ArrayList<>();

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Integer getListPrice() {
        return listPrice;
    }

    public void setListPrice(Integer listPrice) {
        this.listPrice = listPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public Integer getFreight() {
        return freight;
    }

    public void setFreight(Integer freight) {
        this.freight = freight;
    }

    public List<Map<String, String>> getAttr() {
        return attr;
    }

    public void setAttr(List<Map<String, String>> attr) {
        this.attr = attr;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ProductContentDTO{" +
            "productName='" + productName + '\'' +
            ", mainImage='" + mainImage + '\'' +
            ", categoryId=" + categoryId +
            ", brandId=" + brandId +
            ", shopId=" + shopId +
            ", listPrice=" + listPrice +
            ", description='" + description + '\'' +
            ", paymentType=" + paymentType +
            ", address=" + address +
            ", freight=" + freight +
            ", attr=" + attr +
            '}';
    }
}
