package com.wanjun.canalsync.util;

/**
 * Created by wangchengli on 2018/1/29
 */
public enum TableType {
    MAIN("1"),SLAVE("2"),AGGREGATION("3");

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    TableType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TableType{" +
                "value='" + value + '\'' +
                '}';
    }
}
