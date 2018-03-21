package com.wanjun.canalsync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.wanjun.canalsync.event.InsertCanalEvent;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.service.RedisService;
import com.wanjun.canalsync.util.JSONUtil;
import com.wanjun.canalsync.util.SpringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-10
 */
@Component
public class InsertCanalListener extends AbstractCanalListener<InsertCanalEvent> {
    private static final Logger logger = LoggerFactory.getLogger(InsertCanalListener.class);

    @Resource
    private MappingService mappingService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private RedisService redisService;

    @Override
    protected void doSync(String database, String table, String index, String type, RowData rowData, IndexTypeModel indexTypeModel) {
        List<Column> columns = rowData.getAfterColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.warn("insert_column_find_null_warn insert从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }

        Map<String, Object> dataMap = parseColumnsToMap(columns);
        String idValue = idColumn.getValue();
        try {
            sync(database, table, index, type, indexTypeModel, dataMap, idValue);
        } catch (Exception e) {
            logger.error("InsertCanalListener->同步数据失败", e);
            pushTask(database, table, index, type, indexTypeModel, dataMap, idValue,CanalEntry.EventType.INSERT_VALUE);
        }


    }


    public void sync(String database, String table, String index, String type, IndexTypeModel indexTypeModel, Map<String, Object> dataMap, String idValue) throws Exception {
        //ES同步插入
        logger.debug("insert_column_id_info insert主键id,database=" + database + ",table=" + table + ",id=" + idValue);
        elasticsearchService.insertById(index, type, idValue, dataMap);
        logger.debug("insert_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + JSONUtil.toJson(dataMap));

        //redis同步插入
        String redisKey = getMappingKey(index, type);
        redisService.hset(redisKey, idValue, dataMap);
        logger.debug("insert_redis_info 同步redis插入操作成功! database=" + database + ",table=" + table + ",data=" + JSONUtil.toJson(dataMap));

        //插入聚合数据
        logger.debug("聚合数据,database=" + database + ",table=" + table);
        String path = getPath(index, type, CanalEntry.EventType.INSERT.getNumber());
        SpringUtil.doEvent(path, dataMap, indexTypeModel);

    }
}
