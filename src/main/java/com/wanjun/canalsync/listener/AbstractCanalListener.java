package com.wanjun.canalsync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.queue.KMQueueManagerHanler;
import com.wanjun.canalsync.event.CanalEvent;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.model.CanalRowData;
import com.wanjun.canalsync.model.DatabaseTableModel;
import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.TaskQueue;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-10
 */
public abstract class AbstractCanalListener<EVENT extends CanalEvent> implements ApplicationListener<EVENT> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCanalListener.class);

    @Resource
    private MappingService mappingService;

    @Resource
    private KMQueueManagerHanler kmQueueManagerHanler;

    @Override
    public void onApplicationEvent(EVENT event) {
        Entry entry = event.getEntry();
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        IndexTypeModel indexTypeModel = mappingService.getIndexType(new DatabaseTableModel(database, table));
        if (indexTypeModel == null) {
            return;
        }
        String index = indexTypeModel.getIndex();
        String type = indexTypeModel.getType();
        RowChange change;
        try {
            change = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            logger.error("canalEntry_parser_error,根据CanalEntry获取RowChange失败！", e);
            return;
        }
        change.getRowDatasList().forEach(rowData -> doSync(database, table, index, type, rowData, indexTypeModel));
    }

    Map<String, Object> parseColumnsToMap(List<Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getIsNull() ? null : mappingService.getElasticsearchTypeObject(column.getMysqlType(), column.getValue()));
        });
        return jsonMap;
    }

    /**
     * 根据database,tableName组装key
     *
     * @param database
     * @param tableName
     * @return
     */
    protected String getMappingKey(String database, String tableName) {
        return String.format("%s.%s", database, tableName);
    }

    protected String getPath(String database, String tableName, int canalEventTypeNumber) {
        return String.format("/%s/%s/%s", database, tableName, canalEventTypeNumber);

    }



    /**
     * 添加Redis队列任务
     * @param database
     * @param table
     * @param index
     * @param type
     * @param indexTypeModel
     * @param dataMap
     * @param idValue
     * @param eventType
     */
    protected void pushTask(String database, String table, String index, String type, IndexTypeModel indexTypeModel, Map<String, Object> dataMap, String idValue, int eventType) {
        CanalRowData canalRowData = new CanalRowData(database, table, index, type, dataMap, idValue, indexTypeModel, eventType);
        TaskQueue taskQueue = kmQueueManagerHanler.getTaskQueue();
        String data = JSONUtil.toJson(canalRowData);
        Task task = new Task(taskQueue.getName(), null, true, "", data, new Task.TaskStatus());
        taskQueue.pushTask(task);
    }


    protected abstract void doSync(String database, String table, String index, String type, RowData rowData, IndexTypeModel indexTypeModel);
}
