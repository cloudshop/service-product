package com.eyun.product.service.dto;

import javax.validation.constraints.NotNull;

public class ProductSeachParam {

    private Long categoryId;
    @NotNull
    private Long shopId;

    private String productName;

    private String sale;//销量

    private String price;

    private Integer startPrice;

    private Integer endPrice;

    private Integer pageNum;

    private Integer pageSize;

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Integer startPrice) {
        this.startPrice = startPrice;
    }

    public Integer getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Integer endPrice) {
        this.endPrice = endPrice;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "ProductSeachParam{" +
            "categoryId=" + categoryId +
            ", shopId=" + shopId +
            ", productName='" + productName + '\'' +
            ", sale='" + sale + '\'' +
            ", price='" + price + '\'' +
            ", startPrice=" + startPrice +
            ", endPrice=" + endPrice +
            ", pageSize=" + pageSize +
            '}';
    }
}
