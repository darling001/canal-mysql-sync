package com.wanjun.canalsync.model;

import java.io.Serializable;

import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-01
 */
public class CanalRowChange implements Serializable {

    private static final long serialVersionUID = -90027012566550680L;

    private String schemaName;

    private String tableName;

    private RowChange rowChage;

    private EventType eventType;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public RowChange getRowChage() {
        return rowChage;
    }

    public void setRowChage(RowChange rowChage) {
        this.rowChage = rowChage;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
