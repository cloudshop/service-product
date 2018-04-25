package com.eyun.product.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the ProductSku entity. This class is used in ProductSkuResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /product-skus?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProductSkuCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter productId;

    private IntegerFilter count;

    private BigDecimalFilter price;

    private IntegerFilter status;

    private StringFilter skuName;

    private StringFilter skuCode;

    private StringFilter attrString;

    private InstantFilter createdTime;

    private InstantFilter updatedTime;

    private BooleanFilter deleted;

    private BigDecimalFilter transfer;

    public ProductSkuCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }

    public IntegerFilter getCount() {
        return count;
    }

    public void setCount(IntegerFilter count) {
        this.count = count;
    }

    public BigDecimalFilter getPrice() {
        return price;
    }

    public void setPrice(BigDecimalFilter price) {
        this.price = price;
    }

    public IntegerFilter getStatus() {
        return status;
    }

    public void setStatus(IntegerFilter status) {
        this.status = status;
    }

    public StringFilter getSkuName() {
        return skuName;
    }

    public void setSkuName(StringFilter skuName) {
        this.skuName = skuName;
    }

    public StringFilter getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(StringFilter skuCode) {
        this.skuCode = skuCode;
    }

    public StringFilter getAttrString() {
        return attrString;
    }

    public void setAttrString(StringFilter attrString) {
        this.attrString = attrString;
    }

    public InstantFilter getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(InstantFilter createdTime) {
        this.createdTime = createdTime;
    }

    public InstantFilter getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(InstantFilter updatedTime) {
        this.updatedTime = updatedTime;
    }

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public BigDecimalFilter getTransfer() {
        return transfer;
    }

    public void setTransfer(BigDecimalFilter transfer) {
        this.transfer = transfer;
    }

    @Override
    public String toString() {
        return "ProductSkuCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
                (count != null ? "count=" + count + ", " : "") +
                (price != null ? "price=" + price + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (skuName != null ? "skuName=" + skuName + ", " : "") +
                (skuCode != null ? "skuCode=" + skuCode + ", " : "") +
                (attrString != null ? "attrString=" + attrString + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (updatedTime != null ? "updatedTime=" + updatedTime + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
                (transfer != null ? "transfer=" + transfer + ", " : "") +
            "}";
    }

}
