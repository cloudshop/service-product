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
 * Criteria class for the Category entity. This class is used in CategoryResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /categories?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CategoryCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter name;

    private LongFilter parentId;

    private BooleanFilter isParent;

    private StringFilter target;

    private IntegerFilter targetType;

    private InstantFilter createdTime;

    private InstantFilter updatedTime;

    private BooleanFilter deleted;

    private IntegerFilter categoryGrade;

    private StringFilter image;

    private IntegerFilter rank;

    public CategoryCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getParentId() {
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public BooleanFilter getIsParent() {
        return isParent;
    }

    public void setIsParent(BooleanFilter isParent) {
        this.isParent = isParent;
    }

    public StringFilter getTarget() {
        return target;
    }

    public void setTarget(StringFilter target) {
        this.target = target;
    }

    public IntegerFilter getTargetType() {
        return targetType;
    }

    public void setTargetType(IntegerFilter targetType) {
        this.targetType = targetType;
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

    public IntegerFilter getCategoryGrade() {
        return categoryGrade;
    }

    public void setCategoryGrade(IntegerFilter categoryGrade) {
        this.categoryGrade = categoryGrade;
    }

    public StringFilter getImage() {
        return image;
    }

    public void setImage(StringFilter image) {
        this.image = image;
    }

    public IntegerFilter getRank() {
        return rank;
    }

    public void setRank(IntegerFilter rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "CategoryCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (parentId != null ? "parentId=" + parentId + ", " : "") +
                (isParent != null ? "isParent=" + isParent + ", " : "") +
                (target != null ? "target=" + target + ", " : "") +
                (targetType != null ? "targetType=" + targetType + ", " : "") +
                (createdTime != null ? "createdTime=" + createdTime + ", " : "") +
                (updatedTime != null ? "updatedTime=" + updatedTime + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
                (categoryGrade != null ? "categoryGrade=" + categoryGrade + ", " : "") +
                (image != null ? "image=" + image + ", " : "") +
                (rank != null ? "rank=" + rank + ", " : "") +
            "}";
    }

}
