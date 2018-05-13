package com.eyun.product.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A ProductSku.
 */
@Entity
@Table(name = "product_sku")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProductSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @NotNull
    @Min(value = 0)
    @Column(name = "count", nullable = false)
    private Integer count;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision=10, scale=2, nullable = false)
    private BigDecimal price;

    @Column(name = "status")
    private Integer status;

    @Column(name = "sku_name")
    private String skuName;

    @Column(name = "sku_code")
    private String skuCode;

    @Column(name = "attr_string")
    private String attrString;

    @Column(name = "created_time")
    private Instant createdTime;

    @Column(name = "updated_time")
    private Instant updatedTime;

    @Column(name = "deleted")
    private Boolean deleted;

    @DecimalMin(value = "0")
    @DecimalMax(value = "1")
    @Column(name = "transfer", precision=10, scale=2)
    private BigDecimal transfer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public ProductSku productId(Long productId) {
        this.productId = productId;
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCount() {
        return count;
    }

    public ProductSku count(Integer count) {
        this.count = count;
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ProductSku price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public ProductSku status(Integer status) {
        this.status = status;
        return this;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSkuName() {
        return skuName;
    }

    public ProductSku skuName(String skuName) {
        this.skuName = skuName;
        return this;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public ProductSku skuCode(String skuCode) {
        this.skuCode = skuCode;
        return this;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getAttrString() {
        return attrString;
    }

    public ProductSku attrString(String attrString) {
        this.attrString = attrString;
        return this;
    }

    public void setAttrString(String attrString) {
        this.attrString = attrString;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public ProductSku createdTime(Instant createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getUpdatedTime() {
        return updatedTime;
    }

    public ProductSku updatedTime(Instant updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    public void setUpdatedTime(Instant updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public ProductSku deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public BigDecimal getTransfer() {
        return transfer;
    }

    public ProductSku transfer(BigDecimal transfer) {
        this.transfer = transfer;
        return this;
    }

    public void setTransfer(BigDecimal transfer) {
        this.transfer = transfer;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductSku productSku = (ProductSku) o;
        if (productSku.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), productSku.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProductSku{" +
            "id=" + getId() +
            ", productId=" + getProductId() +
            ", count=" + getCount() +
            ", price=" + getPrice() +
            ", status=" + getStatus() +
            ", skuName='" + getSkuName() + "'" +
            ", skuCode='" + getSkuCode() + "'" +
            ", attrString='" + getAttrString() + "'" +
            ", createdTime='" + getCreatedTime() + "'" +
            ", updatedTime='" + getUpdatedTime() + "'" +
            ", deleted='" + isDeleted() + "'" +
            ", transfer=" + getTransfer() +
            "}";
    }
}
