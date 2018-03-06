package com.wanjun.canalsync.util;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-06
 * 查询类型枚举
 */
public enum  SelectType {
    PK("1"),//根据主键查询
    SELF_JOIN("2"); //自关联查询

    private String type;

    SelectType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SelectType{" +
                "type='" + type + '\'' +
                '}';
    }
}
