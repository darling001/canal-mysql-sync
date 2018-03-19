package com.wanjun.canalsync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.wanjun.canalsync.event.UpdateCanalEvent;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.service.RedisService;
import com.wanjun.canalsync.util.JSONUtil;
import com.wanjun.canalsync.util.SpringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomUtils;
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
public class UpdateCanalListener extends AbstractCanalListener<UpdateCanalEvent> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCanalListener.class);

    @Resource
    private MappingService mappingService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private RedisService redisService;

    @Override
    protected void doSync(String database, String table, String index, String type, RowData rowData, IndexTypeModel indexTypeModel) { List<Column> columns = rowData.getAfterColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.warn("update_column_find_null_warn update从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        Map<String, Object> dataMap = parseColumnsToMap(columns);
        String idValue = idColumn.getValue();
        try {
            sync(database, table, index, type, indexTypeModel, dataMap, idValue);
        } catch (Exception e) {
            logger.error("UpdateCanalListener->同步数据失败", e);
            //pushTask(database, table, index, type, indexTypeModel, dataMap, idValue, CanalEntry.EventType.UPDATE_VALUE);
        }

    }

    public void sync(String database, String table, String index, String type, IndexTypeModel indexTypeModel ,Map<String, Object> dataMap, String idValue) throws Exception {
        logger.debug("update_column_id_info update主键id,database=" + database + ",table=" + table + ",id=" + idValue);
        elasticsearchService.update(index, type, idValue, dataMap);
        logger.debug("update_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + dataMap);

        String redisKey = getMappingKey(index, type);
        redisService.hset(redisKey, idValue, dataMap);
        logger.debug("insert_redis_info 同步redis更新操作成功! database=" + database + ",table=" + table + ",data=" + JSONUtil.toJson(dataMap));

        //更新聚合数据
        logger.debug("聚合数据,database=" + database + ",table=" + table);
        String path = getPath(index, type, CanalEntry.EventType.UPDATE.getNumber());
        SpringUtil.doEvent(path, dataMap, indexTypeModel);

    }
}
