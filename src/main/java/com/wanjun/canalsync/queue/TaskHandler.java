package com.wanjun.canalsync.queue;

/**
 * Created by wangchengli on 2018/1/30
 */
public interface TaskHandler {

    /**
     * 业务处理
     *
     * @param data   task任务数据
     * @param params 业务自定义参数
     */
    void handle(String data, Object... params);

}
