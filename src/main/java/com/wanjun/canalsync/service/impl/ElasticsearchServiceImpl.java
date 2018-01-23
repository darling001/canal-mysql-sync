package com.wanjun.canalsync.service.impl;

import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-12
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Resource
    private TransportClient transportClient;
    /**
     * 插入单条索引
     * @param index 索引名
     * @param type  索引类型
     * @param id    索引Id
     * @param dataMap 索引数据
     */
    @Override
    public void insertById(String index, String type, String id, Map<String, Object> dataMap) {
        transportClient.prepareIndex(index, type, id).setSource(dataMap).get();
    }
    /**
     * 批量插入索引（bulk方式）
     * @param index
     * @param type
     * @param idDataMap
     */
    @Override
    public void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap) {
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();

        idDataMap.forEach((id, dataMap) -> bulkRequestBuilder.add(transportClient.prepareIndex(index, type, id).setSource(dataMap)));
        try {
            BulkResponse bulkResponse = bulkRequestBuilder.execute().get();
            if (bulkResponse.hasFailures()) {
                logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSONUtil.toJson(idDataMap) + ", cause:" + bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            logger.error("elasticsearch批量插入错误, index=" + index + ", type=" + type + ", data=" + JSONUtil.toJson(idDataMap), e);
        }
    }
    /**
     * 更新索引
     * @param index
     * @param type
     * @param id
     * @param dataMap
     */
    @Override
    public void update(String index, String type, String id, Map<String, Object> dataMap) {
        this.insertById(index, type, id, dataMap);
    }
    /**
     * 删除索引
     * @param index
     * @param type
     * @param id
     */
    @Override
    public void deleteById(String index, String type, String id) {
        transportClient.prepareDelete(index, type, id).get();
    }

    /**
     * 创建索引
     * @param index
     * @return
     */
    @Override
    public boolean createIndex(String index) {
        if (!isIndexExist(index)) {
            logger.info("Index is not exits!");
        }
        CreateIndexResponse indexresponse = transportClient.admin().indices().prepareCreate(index).execute().actionGet();
        logger.info("执行建立成功？" + indexresponse.isAcknowledged());
        return indexresponse.isAcknowledged();
    }

    /**
     * 删除索引
     * @param index
     * @return
     */
    @Override
    public boolean deleteIndex(String index) {

        if (!isIndexExist(index)) {
            logger.info("Index is not exits!");
        }
        DeleteIndexResponse dResponse = transportClient.admin().indices().prepareDelete(index).execute().actionGet();
        if (dResponse.isAcknowledged()) {
            logger.info("delete index " + index + "  successfully!");
        } else {
            logger.info("Fail to delete index " + index);
        }
        return dResponse.isAcknowledged();

    }

    /**
     * 判断索引是否存在
     * @param index
     * @return
     */
    @Override
    public boolean isIndexExist(String index) {
        IndicesExistsResponse inExistsResponse = transportClient.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
        if (inExistsResponse.isExists()) {
            logger.info("Index [" + index + "] is exist!");
        } else {
            logger.info("Index [" + index + "] is not exist!");
        }
        return inExistsResponse.isExists();
    }

    /**
     * 通过ID获取数据
     * @param index
     * @param type
     * @param id
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    @Override
    public Map<String, Object> searchDataById(String index, String type, String id, String fields) {
        GetRequestBuilder getRequestBuilder = transportClient.prepareGet(index, type, id);
        if (StringUtils.isNotEmpty(fields)) {
            getRequestBuilder.setFetchSource(fields.split(","), null);
        }

        GetResponse getResponse = getRequestBuilder.execute().actionGet();

        return getResponse.getSource();
    }
}
