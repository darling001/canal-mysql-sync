package com.wanjun.canalsync.model;
import java.util.List;
/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-24
 * 聚合model
 */
public class AggregationModel {
    private String index;
    private String type;
    private String mainPKColumn; //主表Id
    private List<String> slavePKColumnList; //从表关联Id
    private String mainTableName;//主表名
    private List<String> slaveTableNameList;//从表名
    private String aggregationTable;//聚合表名
    private String databaseName;//库名

    public AggregationModel() {
    }

    public AggregationModel(String index, String type, String mainPKColumn, List<String> slavePKColumnList, String mainTableName, List<String> slaveTableNameList, String aggregationTable, String databaseName) {
        this.index = index;
        this.type = type;
        this.mainPKColumn = mainPKColumn;
        this.slavePKColumnList = slavePKColumnList;
        this.mainTableName = mainTableName;
        this.slaveTableNameList = slaveTableNameList;
        this.aggregationTable = aggregationTable;
        this.databaseName = databaseName;
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

    public String getMainPKColumn() {
        return mainPKColumn;
    }

    public void setMainPKColumn(String mainPKColumn) {
        this.mainPKColumn = mainPKColumn;
    }

    public List<String> getSlavePKColumnList() {
        return slavePKColumnList;
    }

    public void setSlavePKColumnList(List<String> slavePKColumnList) {
        this.slavePKColumnList = slavePKColumnList;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getMainTableName() {
        return mainTableName;
    }

    public void setMainTableName(String mainTableName) {
        this.mainTableName = mainTableName;
    }

    public List<String> getSlaveTableNameList() {
        return slaveTableNameList;
    }

    public void setSlaveTableNameList(List<String> slaveTableNameList) {
        this.slaveTableNameList = slaveTableNameList;
    }

    public String getAggregationTable() {
        return aggregationTable;
    }

    public void setAggregationTable(String aggregationTable) {
        this.aggregationTable = aggregationTable;
    }
}
