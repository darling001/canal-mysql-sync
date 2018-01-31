package com.wanjun.canalsync.queue;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class EntryTaskHandler implements TaskHandler {
    @Override
    public void handle(String data, Object... params) {
        System.out.println("获取任务数据：" + data);
    }
}
