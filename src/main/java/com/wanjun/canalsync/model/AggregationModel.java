package com.wanjun.canalsync.model;
import java.util.List;
/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-24
 * 聚合model
 */
public class AggregationModel {
    private Integer tableType;
    private String index; //索引
    private String type; //类型

    private String aggregationSFK;
    private String slaveSPK;

    private String mainTableName;//主表名
    private String mainPKColumn; //主表PK
    private List<String> fkColumnList; //主表外键字段名
    private List<String> slaveTableNameList;//从表名
    private List<String> slavePKColumnList; //从表关联PK

    private String aggregationTableName;//聚合表名
    private String aggregationPKColumn;//聚合表PK
    private List<String>aggregationFKColumnList; //聚合表外键字段名
    private String databaseName;//库名

    public AggregationModel() {
    }
    //# 表类型.索引.索引类型.从表PK.聚合表.聚合表PK.库名
    public AggregationModel(Integer tableType,String index, String type,String slaveSPK,String aggregationTableName,String aggregationSFK,String databaseName) {
        this.tableType = tableType;
        this.index = index;
        this.type = type;
        this.slaveSPK = slaveSPK;
        this.aggregationTableName = aggregationTableName;
        this.aggregationSFK = aggregationSFK;
        this.databaseName = databaseName;

    }

    public AggregationModel(Integer tableType, String index, String type, String mainTableName, String mainPKColumn, List<String> fkColumnList, List<String> slaveTableNameList, List<String> slavePKColumnList, String aggregationTableName, String aggregationPKColumn, List<String> aggregationFKColumnList, String databaseName) {
        this.tableType = tableType;
        this.index = index;
        this.type = type;
        this.mainTableName = mainTableName;
        this.mainPKColumn = mainPKColumn;
        this.fkColumnList = fkColumnList;
        this.slaveTableNameList = slaveTableNameList;
        this.slavePKColumnList = slavePKColumnList;
        this.aggregationTableName = aggregationTableName;
        this.aggregationPKColumn = aggregationPKColumn;
        this.aggregationFKColumnList = aggregationFKColumnList;
        this.databaseName = databaseName;
    }

    public Integer getTableType() {
        return tableType;
    }

    public void setTableType(Integer tableType) {
        this.tableType = tableType;
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

    public String getAggregationSFK() {
        return aggregationSFK;
    }

    public void setAggregationSFK(String aggregationSFK) {
        this.aggregationSFK = aggregationSFK;
    }

    public String getSlaveSPK() {
        return slaveSPK;
    }

    public void setSlaveSPK(String slaveSPK) {
        this.slaveSPK = slaveSPK;
    }

    public String getMainTableName() {
        return mainTableName;
    }

    public void setMainTableName(String mainTableName) {
        this.mainTableName = mainTableName;
    }

    public String getMainPKColumn() {
        return mainPKColumn;
    }

    public void setMainPKColumn(String mainPKColumn) {
        this.mainPKColumn = mainPKColumn;
    }

    public List<String> getFkColumnList() {
        return fkColumnList;
    }

    public void setFkColumnList(List<String> fkColumnList) {
        this.fkColumnList = fkColumnList;
    }

    public List<String> getSlaveTableNameList() {
        return slaveTableNameList;
    }

    public void setSlaveTableNameList(List<String> slaveTableNameList) {
        this.slaveTableNameList = slaveTableNameList;
    }

    public List<String> getSlavePKColumnList() {
        return slavePKColumnList;
    }

    public void setSlavePKColumnList(List<String> slavePKColumnList) {
        this.slavePKColumnList = slavePKColumnList;
    }

    public String getAggregationTableName() {
        return aggregationTableName;
    }

    public void setAggregationTableName(String aggregationTableName) {
        this.aggregationTableName = aggregationTableName;
    }

    public String getAggregationPKColumn() {
        return aggregationPKColumn;
    }

    public void setAggregationPKColumn(String aggregationPKColumn) {
        this.aggregationPKColumn = aggregationPKColumn;
    }

    public List<String> getAggregationFKColumnList() {
        return aggregationFKColumnList;
    }

    public void setAggregationFKColumnList(List<String> aggregationFKColumnList) {
        this.aggregationFKColumnList = aggregationFKColumnList;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
