package com.eyun.product.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the SkuImg entity. This class is used in SkuImgResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /sku-imgs?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class SkuImgCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter skuId;

    private StringFilter imgUrl;

    private IntegerFilter type;

    private IntegerFilter rank;

    private InstantFilter createdTime;

    private InstantFilter updatedTime;

    private BooleanFilter deleted;

    public SkuImgCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getSkuId() {
        return skuId;
    }

    public void setSkuId(LongFilter skuId) {
        this.skuId = skuId;
    }

    public StringFilter getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(StringFilter imgUrl) {
        this.imgUrl = imgUrl;
    }

    public IntegerFilter getType() {
        return type;
    }

    public void setType(IntegerFilter type) {
        this.type = type;
    }

    public IntegerFilter getRank() {
        return rank;
    }

    public void setRank(IntegerFilter rank) {
        this.rank = rank;
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

    @Override
    public String toString() {
        return "SkuImgCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (skuId != null ? "skuId=" + skuId + ", " : "") +
                (imgUrl != null ? "imgUrl=" + imgUrl + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (rank != null ? "rank=" + rank + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (updatedTime != null ? "updatedTime=" + updatedTime + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
            "}";
    }

}
