package com.wanjun.canalsync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanjun.canalsync.queue.KMQueueManager;
import com.wanjun.canalsync.queue.Task;
import com.wanjun.canalsync.queue.TaskQueue;
import com.wanjun.canalsync.queue.config.Constant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueueTest {

    private static final Logger logger = LoggerFactory.getLogger(QueueTest.class);
    @Resource
    private RedisTemplate<String,String> redisTemplate ;

    @Test
    public void pushTaskTest() {
        KMQueueManager kmQueueManager = new KMQueueManager.Builder(redisTemplate,"worker2_queue:safe")
                .setAliveTimeout(Constant.ALIVE_TIMEOUT)
                .build();
        //初始化隊列
        kmQueueManager.init();

        // 1.获取队列
        TaskQueue taskQueue = kmQueueManager.getTaskQueue("worker2_queue");
        // 2.创建任务
        JSONObject ob = new JSONObject();
        ob.put("data", "mail proxy task");
        String data = JSON.toJSONString(ob);
        // 参数 uid：如果业务需要区分队列任务的唯一性，请自行生成uid参数，
        // 否则队列默认使用uuid生成策略，这会导致即使data数据完全相同的任务也会被当作两个不同的任务处理。
        // 参数 type：用于业务逻辑的处理，你可以根据不同的type任务类型，调用不同的handler去处理，可以不传。
        Task task = new Task(taskQueue.getName(), "a509bd99-1071-4de1-9220-a280b0a4f47a", true, "", data, new Task.TaskStatus());
        // 3.将任务加入队列
        Task rs = taskQueue.pushTask(task);
        logger.info("pushTask result：" + JSON.toJSONString(rs));
    }

    @Test
    public void popTaskTest() {
        KMQueueManager kmQueueManager = new KMQueueManager.Builder(redisTemplate,"worker2_queue:safe")
                .setAliveTimeout(Constant.ALIVE_TIMEOUT)
                .build();
        //初始化隊列
        kmQueueManager.init();
        // 1.获取队列
        TaskQueue taskQueue = kmQueueManager.getTaskQueue("worker2_queue");
        // 2.获取任务
        Task task = taskQueue.popTask();
        // 业务处理放到TaskConsumersHandler里
        if (task != null) {
            task.doTask(kmQueueManager, MyTaskHandler.class);
        }



    }


}
