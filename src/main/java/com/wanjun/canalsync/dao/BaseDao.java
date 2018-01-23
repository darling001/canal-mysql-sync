package com.wanjun.canalsync.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-12
 * 通用Mapper,实现各库表增删改查方法
 */
@Repository
public interface BaseDao {

    Integer insertByMap(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("params") Map<String, Object> params);

    Long updateByMap(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("params") Map<String, Object> params, @Param("key") String key, @Param("value") Object value);

    Long updateNull(@Param("databaseName") String databaseName, @Param("tableName") String tableName, @Param("params") Map<String, Object> params, @Param("key") String key, @Param("value") Object value);

    Long deleteByPK(@Param("databaseName") String databaseName,@Param("tableName") String tableName,@Param("key")String key,@Param("value") Object value);

    Map<String, Object> selectByPK(@Param("key") String key, @Param("value") Object value, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKs(@Param("key") String key, @Param("valueList") List<Object> valueList, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKsLockInShareMode(@Param("key") String key, @Param("valueList") List<Object> valueList, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long count(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long selectMaxPK(@Param("key") String key, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    Long selectMinPK(@Param("key") String key, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKInterval(@Param("key") String key, @Param("minPK") long minPK, @Param("maxPK") long maxPK, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByPKIntervalLockInShareMode(@Param("key") String key, @Param("minPK") long minPK, @Param("maxPK") long maxPK, @Param("databaseName") String databaseName, @Param("tableName") String tableName);

}
