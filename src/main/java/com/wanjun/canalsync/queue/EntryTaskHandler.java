package com.wanjun.canalsync.queue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.wanjun.canalsync.model.CanalRowChange;
import com.wanjun.canalsync.util.JSONUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-31
 */
public class EntryTaskHandler implements TaskHandler, ApplicationContextAware {
    private ApplicationContext applicationContext = null;

    @Override
    public void handle(String data, Object... params) {
        System.out.println("获取任务数据：" + data);
        JSONObject change = JSON.parseObject(data);


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
