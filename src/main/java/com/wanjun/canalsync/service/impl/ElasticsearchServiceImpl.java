package com.wanjun.canalsync.service.impl;

import com.wanjun.canalsync.model.EsPage;
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
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 使用分词查询
     *
     * @param index     索引名称
     * @param type      类型名称,可传入多个type逗号分隔
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param size      文档大小限制
     * @param matchStr  过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    public List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String matchStr) {
        return searchListData(index, type, startTime, endTime, size, null, null, false, null, matchStr);
    }

    /**
     * 使用分词查询
     *
     * @param index    索引名称
     * @param type     类型名称,可传入多个type逗号分隔
     * @param size     文档大小限制
     * @param fields   需要显示的字段，逗号分隔（缺省为全部字段）
     * @param matchStr 过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    public  List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, null, false, null, matchStr);
    }

    /**
     * 使用分词查询
     * @param index       索引名称
     * @param type        类型名称,可传入多个type逗号分隔
     * @param size        文档大小限制
     * @param fields      需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField   排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param matchStr    过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    public  List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, null, matchStr);
    }


    /**
     * 使用分词查询
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    public  List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {
        return searchListData(index, type, 0, 0, size, fields, sortField, matchPhrase, highlightField, matchStr);
    }

    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    public List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (startTime > 0 && endTime > 0) {
            boolQuery.must(QueryBuilders.rangeQuery("timestamp")
                    .format("epoch_millis")
                    .from(startTime)
                    .to(endTime)
                    .includeLower(true)
                    .includeUpper(true));
        }

        //搜索的的字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(",")) {
                String[] ss = s.split("=");
                if (ss.length > 1) {
                    if (matchPhrase == Boolean.TRUE) {
                        boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]));
                    } else {
                        boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
                    }
                }

            }
        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotEmpty(highlightField)) {


            // 设置高亮字段
            searchRequestBuilder.addHighlightedField(highlightField);
            searchRequestBuilder.setHighlighterPreTags("<span style='color:red' >");//设置前缀
            searchRequestBuilder.setHighlighterPostTags("</span>");//设置后缀

        }


        searchRequestBuilder.setQuery(boolQuery);

        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }
        searchRequestBuilder.setFetchSource(true);

        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        if (size != null && size > 0) {
            searchRequestBuilder.setSize(size);
        }

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        logger.info("\n{}", searchRequestBuilder);

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits();
        long length = searchResponse.getHits().getHits().length;

        logger.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(searchResponse, highlightField);
        }

        return null;

    }

    /**
     * 使用分词查询,并分页
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param currentPage    当前页
     * @param pageSize       每页显示条数
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param matchPhrase    true 使用，短语精准匹配
     * @param highlightField 高亮字段
     * @param matchStr       过滤条件（xxx=111,aaa=222）
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public  EsPage searchDataPage(String index, String type, int currentPage, int pageSize, long startTime, long endTime, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr) {

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch(index);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type.split(","));
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);

        // 需要显示的字段，逗号分隔（缺省为全部字段）
        if (StringUtils.isNotEmpty(fields)) {
            searchRequestBuilder.setFetchSource(fields.split(","), null);
        }

        //排序字段
        if (StringUtils.isNotEmpty(sortField)) {
            searchRequestBuilder.addSort(sortField, SortOrder.DESC);
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (startTime > 0 && endTime > 0) {
            boolQuery.must(QueryBuilders.rangeQuery("timestamp")
                    .format("epoch_millis")
                    .from(startTime)
                    .to(endTime)
                    .includeLower(true)
                    .includeUpper(true));
        }

        // 查询字段
        if (StringUtils.isNotEmpty(matchStr)) {
            for (String s : matchStr.split(",")) {
                String[] ss = s.split("=");
                if (matchPhrase == Boolean.TRUE) {
                    boolQuery.must(QueryBuilders.matchPhraseQuery(s.split("=")[0], s.split("=")[1]));
                } else {
                    boolQuery.must(QueryBuilders.matchQuery(s.split("=")[0], s.split("=")[1]));
                }
            }
        }

        // 高亮（xxx=111,aaa=222）
        if (StringUtils.isNotEmpty(highlightField)) {
            // 设置高亮字段
            searchRequestBuilder.addHighlightedField(highlightField);
            searchRequestBuilder.setHighlighterPreTags("<span style='color:red' >");//设置前缀
            searchRequestBuilder.setHighlighterPostTags("</span>");//设置后缀

        }

        searchRequestBuilder.setQuery(QueryBuilders.matchAllQuery());
        searchRequestBuilder.setQuery(boolQuery);

        // 分页应用
        //searchRequestBuilder.setSize(pageSize);

        // 分页应用
        searchRequestBuilder.setFrom(0).setSize(pageSize);

        // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);

        //打印的内容 可以在 Elasticsearch head 和 Kibana  上执行查询
        logger.info("\n{}", searchRequestBuilder);

        // 执行搜索,返回搜索响应信息
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        long totalHits = searchResponse.getHits().totalHits();
        long length = searchResponse.getHits().getHits().length;

        logger.info("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (searchResponse.status().getStatus() == 200) {
            // 解析对象
            List<Map<String, Object>> sourceList = setSearchResponse(searchResponse, highlightField);

            return new EsPage(currentPage, pageSize, (int) totalHits, sourceList);
        }

        return null;
    }

    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            //searchHit.getSource().put("id", searchHit.getId());

            if (StringUtils.isNotEmpty(highlightField)) {

                logger.info("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSource());
                Map<String, HighlightField> stringHighlightFieldMap = searchHit.highlightFields();
                Text[] text = searchHit.highlightFields().get(highlightField).fragments();

                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
                    //遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSource().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSource());
        }

        return sourceList;
    }
}
