package com.wanjun.canalsync.client;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.wanjun.canalsync.event.DeleteCanalEvent;
import com.wanjun.canalsync.event.InsertCanalEvent;
import com.wanjun.canalsync.event.UpdateCanalEvent;
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
    private static String canal_get = "get_message batchId : {} , entrySize : {}";
    private static String canal_ack = "ack_message batchId : {} ";

    private String destination;
    private ApplicationContext applicationContext;

    public MultiThreadCanalClient(String destination, CanalConnector connector, ApplicationContext applicationContext) {
        this.destination = destination;
        this.connector = connector;
        this.applicationContext = applicationContext;
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
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MDC.remove("destination");
    }

    private void process() {
        int batchSize = 5 * 1024;
        long batchId = 0; // message batchId
        while (running) {
            try {
                MDC.put("destination", destination);
                connector.connect();
                connector.subscribe();
                // 回滚寻找上次中断的位置
                connector.rollback();

                while (running) {
                    Message message = connector.getWithoutAck(batchSize);//获取指定数量的数据
                    batchId = message.getId();
                    List<CanalEntry.Entry> entries = message.getEntries();
                    int size = entries.size();
                    if (batchId != -1 && size > 0) {
                        logger.info(canal_get, batchId, size);
                        entries.forEach(entry -> {
                            if (entry.getEntryType().equals(CanalEntry.EntryType.ROWDATA)) {
                                publishCanalEvent(entry);
                            }
                        });

                        logger.info(canal_ack, batchId);
                    }
                    connector.ack(batchId);//提交确认
                }
            } catch (Exception e) {
                logger.error("发送监听事件失败！batchId回滚,batchId=" + batchId, e);
                //出错了以后不进行回滚，否则会造成死循环，不停的进行ack
                //应该记录错误信息，进行异步任务处理 TODO
                //connector.rollback(batchId);
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
}
