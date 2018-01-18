package com.wanjun.canalsync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.wanjun.canalsync.event.InsertCanalEvent;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.util.JsonUtil;
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

    @Override
    protected void doSync(String database, String table, String index, String type, RowData rowData) {
        List<Column> columns = rowData.getAfterColumnsList();
       /* columns.forEach(column -> {
            logger.error(column.getName() + "," + column.getIsKey());
        });*/
        String primaryKey = Optional.ofNullable(mappingService.getTablePrimaryKeyMap().get(database + "." + table)).orElse("id");
        Column idColumn = columns.stream().filter(column -> primaryKey.equals(column.getName())).findFirst().orElse(null);
        if (idColumn == null || StringUtils.isBlank(idColumn.getValue())) {
            logger.warn("insert_column_find_null_warn insert从column中找不到主键,database=" + database + ",table=" + table);
            return;
        }
        logger.debug("insert_column_id_info insert主键id,database=" + database + ",table=" + table + ",id=" + idColumn.getValue());
        Map<String, Object> dataMap = parseColumnsToMap(columns);
        elasticsearchService.insertById(index, type, idColumn.getValue(), dataMap);
        logger.debug("insert_es_info 同步es插入操作成功！database=" + database + ",table=" + table + ",data=" + JsonUtil.toJson(dataMap));
    }
}
