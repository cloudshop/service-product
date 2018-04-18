package com.eyun.product.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductContentDTO implements Serializable {
    private String productName;
    private String mainImage;//商品主图
    private Long brandId;
    private Long shopId;
    private System article_number;//商家货号
    private Integer listPrice;
    private String description;//商品介绍
    private Integer paymentType;//支付方式
    private Double skuPrice;//商品单价
    private String transfer;//让利百分比 如10%
    private Long address;//商品所在地
    private Integer skuCount;//sku单价
    private Double freight;//运费
    private List<Map<String,String>> attr=new ArrayList<>();
    private List<String>skuImageUrl=new ArrayList<>();//sku图

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

    public System getArticle_number() {
        return article_number;
    }

    public void setArticle_number(System article_number) {
        this.article_number = article_number;
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

    public Double getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(Double skuPrice) {
        this.skuPrice = skuPrice;
    }

    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    public Long getAddress() {
        return address;
    }

    public void setAddress(Long address) {
        this.address = address;
    }

    public Integer getSkuCount() {
        return skuCount;
    }

    public void setSkuCount(Integer skuCount) {
        this.skuCount = skuCount;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public List<Map<String, String>> getAttr() {
        return attr;
    }

    public void setAttr(List<Map<String, String>> attr) {
        this.attr = attr;
    }

    public List<String> getSkuImageUrl() {
        return skuImageUrl;
    }

    public void setSkuImageUrl(List<String> skuImageUrl) {
        this.skuImageUrl = skuImageUrl;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    @Override
    public String toString() {
        return "ProductContentDTO{" +
            "productName='" + productName + '\'' +
            ", mainImage='" + mainImage + '\'' +
            ", brandId=" + brandId +
            ", article_number=" + article_number +
            ", listPrice=" + listPrice +
            ", description='" + description + '\'' +
            ", paymentType=" + paymentType +
            ", skuPrice=" + skuPrice +
            ", transfer='" + transfer + '\'' +
            ", address=" + address +
            ", skuCount=" + skuCount +
            ", freight=" + freight +
            ", attr=" + attr +
            ", skuImageUrl=" + skuImageUrl +
            '}';
    }
}
