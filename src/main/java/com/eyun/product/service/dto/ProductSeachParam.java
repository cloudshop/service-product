package com.eyun.product.service.dto;

public class ProductSeachParam {
    private Long categoryId;

    private String productName;

    private String sale;//销量

    private String price;

    private Integer startPrice;

    private Integer endPrice;

    public Long getCategoryId() {
        return categoryId;
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

    @Override
    public String toString() {
        return "ProductSeachParam{" +
            "categoryId=" + categoryId +
            ", productName='" + productName + '\'' +
            ", sale='" + sale + '\'' +
            ", price='" + price + '\'' +
            ", startPrice=" + startPrice +
            ", endPrice=" + endPrice +
            '}';
    }
}
