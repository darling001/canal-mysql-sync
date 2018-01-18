package com.wanjun.canalsync.service;


import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.model.request.SyncByTableRequest;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-10
 */
public interface SyncService {
    /**
     * 通过database和table同步数据库
     *
     * @param request 请求参数
     * @return 后台同步进程执行成功与否
     */
    boolean syncByTable(SyncByTableRequest request);

    /**
     * 开启事务的读取mysql并插入到Elasticsearch中（读锁）
     */
    void batchInsertElasticsearch(SyncByTableRequest request, String primaryKey, long from, long to, IndexTypeModel indexTypeModel);
}
