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
 * Criteria class for the Brand entity. This class is used in BrandResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /brands?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class BrandCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter categoryId;

    private StringFilter brandName;

    private StringFilter brandAlias;

    private StringFilter description;

    private StringFilter logo;

    private IntegerFilter status;

    private InstantFilter createdTime;

    private InstantFilter updatedTime;

    private IntegerFilter deleted;

    public BrandCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public StringFilter getBrandName() {
        return brandName;
    }

    public void setBrandName(StringFilter brandName) {
        this.brandName = brandName;
    }

    public StringFilter getBrandAlias() {
        return brandAlias;
    }

    public void setBrandAlias(StringFilter brandAlias) {
        this.brandAlias = brandAlias;
    }

    public StringFilter getDescription() {
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getLogo() {
        return logo;
    }

    public void setLogo(StringFilter logo) {
        this.logo = logo;
    }

    public IntegerFilter getStatus() {
        return status;
    }

    public void setStatus(IntegerFilter status) {
        this.status = status;
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

    public IntegerFilter getDeleted() {
        return deleted;
    }

    public void setDeleted(IntegerFilter deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "BrandCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
                (brandName != null ? "brandName=" + brandName + ", " : "") +
                (brandAlias != null ? "brandAlias=" + brandAlias + ", " : "") +
                (description != null ? "description=" + description + ", " : "") +
                (logo != null ? "logo=" + logo + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (updatedTime != null ? "updatedTime=" + updatedTime + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
            "}";
    }

}
