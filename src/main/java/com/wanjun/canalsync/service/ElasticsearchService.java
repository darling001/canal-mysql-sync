package com.wanjun.canalsync.service;

import com.wanjun.canalsync.model.EsPage;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-25
 */
public interface ElasticsearchService {

    /**
     * 插入单条索引
     *
     * @param index   索引名
     * @param type    索引类型
     * @param id      索引Id
     * @param dataMap 索引数据
     */
    void insertById(String index, String type, String id, Map<String, Object> dataMap);

    /**
     * 批量插入索引（bulk方式）
     *
     * @param index
     * @param type
     * @param idDataMap
     */
    void batchInsertById(String index, String type, Map<String, Map<String, Object>> idDataMap);

    /**
     * 更新索引
     *
     * @param index
     * @param type
     * @param id
     * @param dataMap
     */
    void update(String index, String type, String id, Map<String, Object> dataMap);

    /**
     * 删除索引
     *
     * @param index
     * @param type
     * @param id
     */
    void deleteById(String index, String type, String id);

    //---------------------封装es查询方法----------------------------

    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    boolean createIndex(String index);

    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    boolean deleteIndex(String index);

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    boolean isIndexExist(String index);

    /**
     * 通过ID获取数据
     *
     * @param index
     * @param type
     * @param id
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    Map<String, Object> searchDataById(String index, String type, String id, String fields);


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
    List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String matchStr);


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
    List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String matchStr);

    /**
     * 使用分词查询
     *
     * @param index       索引名称
     * @param type        类型名称,可传入多个type逗号分隔
     * @param size        文档大小限制
     * @param fields      需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField   排序字段
     * @param matchPhrase true 使用，短语精准匹配
     * @param matchStr    过滤条件（xxx=111,aaa=222）
     * @return
     */
    List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String matchStr);

    /**
     * 使用分词查询
     *
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
    List<Map<String, Object>> searchListData(String index, String type, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr);

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
    List<Map<String, Object>> searchListData(String index, String type, long startTime, long endTime, Integer size, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr);

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
    public EsPage searchDataPage(String index, String type, int currentPage, int pageSize, long startTime, long endTime, String fields, String sortField, boolean matchPhrase, String highlightField, String matchStr);

    /**
     * 创建ItemAgg映射
     * @param index
     * @param type
     */
    public void createItemAggMapp(String index,String type) ;
}