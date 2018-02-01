package com.wanjun.canalsync.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.Message;
import com.wanjun.canalsync.event.DeleteCanalEvent;
import com.wanjun.canalsync.event.InsertCanalEvent;
import com.wanjun.canalsync.event.UpdateCanalEvent;
import com.wanjun.canalsync.model.CanalRowChange;
import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-18
 */
public class MultiThreadCanalClient {

    private final static Logger logger = LoggerFactory.getLogger(CanalClient.class);

    private volatile boolean running = false;

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread t, Throwable e) {
            logger.error("parse events has an error", e);
        }
    };
    private Thread thread = null;
    private CanalConnector connector;

    private long batchId = -1;

    private static String canal_get = "get_message batchId : {} , entrySize : {}";
    private static String canal_ack = "ack_message batchId : {} ";

    private String destination;
    private ApplicationContext applicationContext;
    private TaskQueue taskQueue = null;


    public MultiThreadCanalClient(String destination, CanalConnector connector, ApplicationContext applicationContext, TaskQueue taskQueue) {
        this.destination = destination;
        this.connector = connector;
        this.applicationContext = applicationContext;
        this.taskQueue = taskQueue;
    }

    public void start() {
        Assert.notNull(connector, "connector is null");
        thread = new Thread(new Runnable() {
            public void run() {
                logger.info("destination:{} running", destination);
                process();
            }
        });
        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running = true;
    }

    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        //停止服务异常处理，回滚上次ack的位置
        if (batchId != -1) {
            connector.rollback(batchId);
        }
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("MultiThreadCanalClient->stop() error", e);
            }
        }

        MDC.remove("destination");
    }

    private void process() {
        int batchSize = 5 * 1024;
        // long batchId = 0; // message batchId
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe();
                // 回滚寻找上次中断的位置
                connector.rollback();
                CanalRowChange change = null;
                while (running) {
                    try {
                        Message message = connector.getWithoutAck(batchSize);//获取指定数量的数据
                        batchId = message.getId();
                        List<CanalEntry.Entry> entries = message.getEntries();
                        int size = entries.size();
                        if (batchId != -1 && size > 0) {
                            logger.info(canal_get, batchId, size);
                            for (CanalEntry.Entry entry : entries) {
                                if (entry.getEntryType().equals(CanalEntry.EntryType.ROWDATA)) {
                                    change = bulidCanalRowChange(entry);
                                    publishCanalEvent(entry);
                                }
                            }
                            logger.info(canal_ack, batchId);
                        }

                        connector.ack(batchId);//提交确认
                    } catch (Exception e) {
                        logger.error("发送监听事件失败！batchId回滚,batchId=" + batchId, e);
                        //异常处理存储到Redis队列中 ，后续使用异步任务处理
                        String data = JSON.toJSONString(change);
                        Task task = new Task(taskQueue.getName(), null, true, "", data, new Task.TaskStatus());
                        //将任务加入队列
                        taskQueue.pushTask(task);
                        connector.ack(batchId);//提交确认
                    }
                }
            } catch (Exception e) {
                logger.error("Canal Client Thread error", e);
            } finally {
                connector.disconnect();
                MDC.remove("destination");
            }
        }
    }

    /**
     * ApplicationContext发布事件
     *
     * @param entry
     */
    @SuppressWarnings("all")
    private void publishCanalEvent(CanalEntry.Entry entry) {
        CanalEntry.EventType eventType = entry.getHeader().getEventType();
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

    private CanalRowChange bulidCanalRowChange(CanalEntry.Entry entry) {
        CanalEntry.RowChange rowChage = null;
        try {
            rowChage = RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
        }
        EventType eventType = rowChage.getEventType();
        String schemaName = entry.getHeader().getSchemaName();
        String tableName = entry.getHeader().getTableName();


        CanalRowChange change = new CanalRowChange();
        change.setSchemaName(schemaName);
        change.setEventType(eventType);
        change.setRowChage(rowChage);
        change.setTableName(tableName);
        return change;
    }
}
