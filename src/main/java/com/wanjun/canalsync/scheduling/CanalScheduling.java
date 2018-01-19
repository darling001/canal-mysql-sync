package com.wanjun.canalsync.scheduling;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.wanjun.canalsync.event.DeleteCanalEvent;
import com.wanjun.canalsync.event.InsertCanalEvent;
import com.wanjun.canalsync.event.UpdateCanalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-03
 */
//@Component
public class CanalScheduling implements Runnable, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CanalScheduling.class);
    private ApplicationContext applicationContext;

    @Resource
    private CanalConnector canalConnector;

    //行数据日志
    private static String row_format = "binlog[{}:{}] , name[{},{}] , eventType : {} , executeTime : {} , delay : {}ms";
    //事务入职
    private static String transaction_format = "binlog[{}:{}] , executeTime : {} , delay : {}ms";
    //数据存储耗时日志
    private static String execute_format = "name[{},{}] , eventType : {} , rows : {} consume : {}ms";


    private static String canal_get = "get_message batchId : {} , entrySize : {}";
    private static String canal_ack = "ack_message batchId : {} ";

    @Scheduled(fixedDelay = 100)
    @Override
    public void run() {
        try {
            int batchSize = 1000;
            Message message = canalConnector.getWithoutAck(batchSize);
            long batchId = message.getId();
            try {
                List<Entry> entries = message.getEntries();
                int size = entries.size();
                if (batchId != -1 && size > 0) {
                    logger.info(canal_get, batchId, size);
                    entries.forEach(entry -> {

                        if (entry.getEntryType() == EntryType.ROWDATA) {
                            publishCanalEvent(entry);
                        }

                    });
                    logger.info(canal_ack, batchId);
                }
                canalConnector.ack(batchId);
            } catch (Exception e) {
                logger.error("发送监听事件失败！batchId回滚,batchId=" + batchId, e);
                canalConnector.rollback(batchId);
            }
        } catch (Exception e) {
            logger.error("canal_scheduled异常！", e);
        }
    }
    @SuppressWarnings("all")
    private void publishCanalEvent(Entry entry) {
        EventType eventType = entry.getHeader().getEventType();
        switch (eventType) {
            case INSERT:
                applicationContext.publishEvent(new InsertCanalEvent(entry));
                break;
            case UPDATE:
                applicationContext.publishEvent(new UpdateCanalEvent(entry));
                break;
            case DELETE:
                applicationContext.publishEvent(new DeleteCanalEvent(entry));
                break;
            default:
                break;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
