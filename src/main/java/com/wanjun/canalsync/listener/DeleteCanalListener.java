package com.wanjun.canalsync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.wanjun.canalsync.event.DeleteCanalEvent;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.service.RedisService;
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
public class DeleteCanalListener extends AbstractCanalListener<DeleteCanalEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCanalListener.class);

    @Resource
    private MappingService mappingService;

    @Resource
    private ElasticsearchService elasticsearchService;

    @Resource
    private RedisService redisService;

    @Override
    protected void doSync(String database, String table, String index, String type, RowData rowData, IndexTypeModel indexTypeModel) {
        List<Column> columns = rowData.getBeforeColumnsList();
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.warn("insert_column_find_null_warn insert从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        Map<String, Object> dataMap = parseColumnsToMap(columns);
        String idValue = idColumn.getValue();
        try {
            sync(database, table, index, type, indexTypeModel, dataMap,idValue);
        }catch (Exception e) {
            logger.error("DeleteCanalListener ->同步数据失败", e);
            //pushTask(database, table, index, type, indexTypeModel, dataMap, idValue,CanalEntry.EventType.DELETE_VALUE);
        }
    }

    public void sync(String database, String table, String index, String type, IndexTypeModel indexTypeModel,  Map<String, Object> dataMap,String idValue) throws Exception{
        logger.debug("delete_column_id_info insert主键id,database=" + database + ",table=" + table + ",id=" + idValue);
        elasticsearchService.deleteById(index, type, idValue);
        logger.debug("delete_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",id=" + idValue);

        String redisKey = getMappingKey(index, type);
        redisService.hdel(redisKey,idValue);
        logger.debug("insert_redis_info 同步redis删除操作成功! database=" + database + ",table=" + table + ",redisKey=" + redisKey);

        //更新聚合数据
        logger.debug("聚合数据,database=" + database + ",table=" + table);
        String path = getPath(index, type, CanalEntry.EventType.DELETE.getNumber());
        SpringUtil.doEvent(path, dataMap, indexTypeModel);

    }
}
