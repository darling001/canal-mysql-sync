package com.wanjun.canalsync.service;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-25
 */
public interface ElasticsearchService {
    void insertById(String index, String type, String id, Map<String, Object> dataMap);

    void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap);

    void update(String index, String type, String id, Map<String, Object> dataMap);

    void deleteById(String index, String type, String id);
}
