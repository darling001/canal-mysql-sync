package com.wanjun.canalsync.queue;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wanjun.canalsync.listener.DeleteCanalListener;
import com.wanjun.canalsync.listener.InsertCanalListener;
import com.wanjun.canalsync.listener.UpdateCanalListener;
import com.wanjun.canalsync.model.CanalRowData;
import com.wanjun.canalsync.util.JSONUtil;
import com.wanjun.canalsync.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class EntryTaskHandler implements TaskHandler {
    private static final Logger logger = LoggerFactory.getLogger(EntryTaskHandler.class);

    @Override
    public void handle(String data, Object... params) throws Throwable{
        logger.info("获取任务数据：" + data);
        CanalRowData canalRowData = JSONUtil.toBean(data, CanalRowData.class);

        int eventType = canalRowData.getEventType();
        switch (eventType) {
            case CanalEntry.EventType.INSERT_VALUE:
                InsertCanalListener insertBean = SpringUtil.getBean(InsertCanalListener.class);
                insertBean.sync(canalRowData.getDatabase(), canalRowData.getTable(),
                        canalRowData.getIndex(), canalRowData.getType(), canalRowData.getIndexTypeModel(), canalRowData.getDataMap(), canalRowData.getIdValue());
                break;
            case CanalEntry.EventType.UPDATE_VALUE:
                UpdateCanalListener updateBean = SpringUtil.getBean(UpdateCanalListener.class);
                updateBean.sync(canalRowData.getDatabase(), canalRowData.getTable(),
                        canalRowData.getIndex(), canalRowData.getType(), canalRowData.getIndexTypeModel(), canalRowData.getDataMap(), canalRowData.getIdValue());
                break;
            case CanalEntry.EventType.DELETE_VALUE:
                DeleteCanalListener deleteBean = SpringUtil.getBean(DeleteCanalListener.class);
                deleteBean.sync(canalRowData.getDatabase(), canalRowData.getTable(),
                        canalRowData.getIndex(), canalRowData.getType(), canalRowData.getIndexTypeModel(), canalRowData.getDataMap(), canalRowData.getIdValue());
            default:
                break;
        }

    }


}
