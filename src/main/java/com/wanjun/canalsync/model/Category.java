package com.wanjun.canalsync.model;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-08
 */
public class Category {

    private String categoryId;
    private String categoryCode;
    private String categoryName;
    private String categoryLevel;

    private Category child;
    public Category() {

    }
    public Category(String categoryId, String categoryCode, String categoryName, String categoryLevel) {
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(String categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public Category getChild() {
        return child;
    }

    public void setChild(Category child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryLevel='" + categoryLevel + '\'' +
                '}';
    }
}
