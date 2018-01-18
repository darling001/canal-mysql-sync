package com.wanjun.canalsync.model;

import com.google.common.base.Objects;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-18
 */
public class IndexTypeModel {
    private String index;
    private String type;

    public IndexTypeModel() {
    }

    public IndexTypeModel(String index, String type) {
        this.index = index;
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexTypeModel that = (IndexTypeModel) o;
        return Objects.equal(index, that.index) &&
                Objects.equal(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(index, type);
    }
}

