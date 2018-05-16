package com.wanjun.canalsync.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-14
 */
public class SpecAttribute {

    @SerializedName("PRICE_FLAG")
    private String priceFlag;

    @SerializedName("ATTRIBUTE_NAME")
    private String attributeName;

    @SerializedName("ATTRIBUTE_VALUE")
    private String attributeValue;

    @SerializedName("ORDER_SORT")
    private String orderSort;

    public SpecAttribute() {
    }

    public SpecAttribute(String priceFlag, String attributeName, String attributeValue,String orderSort) {
        this.priceFlag = priceFlag;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.orderSort = orderSort;
    }

    public String getPriceFlag() {
        return priceFlag;
    }

    public void setPriceFlag(String priceFlag) {
        this.priceFlag = priceFlag;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getOrderSort() {
        return orderSort;
    }

    public void setOrderSort(String orderSort) {
        this.orderSort = orderSort;
    }

    @Override
    public String toString() {
        return "SpecAttribute{" +
                "priceFlag='" + priceFlag + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", attributeValue='" + attributeValue + '\'' +
                ", orderSort='" + orderSort + '\'' +
                '}';
    }
}
