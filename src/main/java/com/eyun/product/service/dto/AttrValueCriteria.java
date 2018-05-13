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
 * Criteria class for the AttrValue entity. This class is used in AttrValueResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /attr-values?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AttrValueCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private LongFilter attrId;

    private StringFilter value;

    private StringFilter image;

    private IntegerFilter status;

    private InstantFilter createdTime;

    private InstantFilter updatedTime;

    private BooleanFilter deleted;

    private IntegerFilter rank;

    public AttrValueCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getAttrId() {
        return attrId;
    }

    public void setAttrId(LongFilter attrId) {
        this.attrId = attrId;
    }

    public StringFilter getValue() {
        return value;
    }

    public void setValue(StringFilter value) {
        this.value = value;
    }

    public StringFilter getImage() {
        return image;
    }

    public void setImage(StringFilter image) {
        this.image = image;
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

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public IntegerFilter getRank() {
        return rank;
    }

    public void setRank(IntegerFilter rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "AttrValueCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (attrId != null ? "attrId=" + attrId + ", " : "") +
                (value != null ? "value=" + value + ", " : "") +
                (image != null ? "image=" + image + ", " : "") +
                (status != null ? "status=" + status + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (updatedTime != null ? "updatedTime=" + updatedTime + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
                (rank != null ? "rank=" + rank + ", " : "") +
            "}";
    }

}
