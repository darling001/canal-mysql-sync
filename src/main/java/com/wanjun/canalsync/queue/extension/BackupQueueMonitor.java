package com.wanjun.canalsync.queue.extension;

import com.alibaba.fastjson.JSON;
import com.wanjun.canalsync.queue.*;
import com.wanjun.canalsync.queue.backup.BackupQueue;
import com.wanjun.canalsync.queue.backup.RedisBackupQueue;
import com.wanjun.canalsync.queue.config.Constant;
import com.wanjun.canalsync.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-30
 */
public class BackupQueueMonitor extends KMQueueAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackupQueueMonitor.class);

    /**
     * 任务超时重试次数
     */
    private int retryTimes;

    /**
     * 失败任务（重试三次失败）的处理方式
     */
    private Pipeline pipeline;

    /**
     * 任务的存活超时时间。注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间。单位：ms
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * 不设置默认为 Long.MAX_VALUE
     */
    private long aliveTimeout;


    /**
     * 任务执行的超时时间（一次执行），单位：ms
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * 不设置默认为 Long.MAX_VALUE
     */
    private long protectedTimeout;


    /**
     * 健康检查
     */
    private AliveDetectHandler aliveDetectHandler;

    /**
     * 备份队列
     */
    private BackupQueue backupQueue;

    /**
     * 构造方法私有化，防止外部调用
     */
    private BackupQueueMonitor() {
    }

    public int getRetryTimes() {
        return retryTimes;
    }


    /**
     * 任务的存活超时时间。注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间。单位：ms
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * 不设置默认为 Long.MAX_VALUE
     *
     * @return 任务的存活时间
     */
    @Override
    public long getAliveTimeout() {
        return aliveTimeout;
    }

    /**
     * 任务执行的超时时间（一次执行），单位：ms
     * <p>
     * 该值只针对安全队列起作用
     * <p>
     * 不设置默认为 Long.MAX_VALUE
     *
     * @return 任务执行的超时时间
     */
    public long getProtectedTimeout() {
        return protectedTimeout;
    }


    /**
     * 启动监控
     */
    public void monitor() {
        Task task;
        try {
            String backUpQueueName = this.getBackUpQueueName();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logger.info("Backup queue[" + backUpQueueName + "]Monitoring begins：" + format.format(new Date()));
            task = backupQueue.popTask();
            while (task != null &&
                    !backUpQueueName.equals(task.getQueue()) &&
                    !RedisBackupQueue.MARKER.equals(task.getType())) {

                /**
                 * 判断任务状态，分别处理
                 * 1. 任务执行超时，且重试次数大于等于retry指定次数，则持久化到数据库
                 * 2. 任务执行超时，且重试次数小于retry指定次数，则重新放入任务队列
                 * 最后，如果满足以上条件，同时删除备份队列中的该任务
                 */
                TaskQueue taskQueue = new RedisTaskQueue(this, task.getQueue(), KMQueueManager.SAFE);
                // 获取任务状态
                Task.TaskStatus status = task.getTaskStatus();

                long currentTimeMillis = System.currentTimeMillis();// 当前时间戳
                long taskGenTimeMillis = status.getGenTimestamp();// 任务生成的时间戳
                long intervalTimeMillis = currentTimeMillis - taskGenTimeMillis;// 任务的存活时间
                if (intervalTimeMillis > this.aliveTimeout) {
                    if (pipeline != null) {
                        pipeline.process(taskQueue, task);// 彻底失败任务的处理
                    }
                    // 删除备份队列中的该任务
                    backupQueue.finishTask(task);
                }

                long taskExcTimeMillis = status.getExcTimestamp();// 任务执行的时间戳
                intervalTimeMillis = currentTimeMillis - taskExcTimeMillis;// 任务此次执行时间

                if (intervalTimeMillis > this.protectedTimeout) {// 任务执行超时

                    // 增加心跳健康检测
                    if (aliveDetectHandler != null) {

                        boolean isAlive = aliveDetectHandler.check(this, task);
                        if (isAlive) {// 当前任务还在执行
                            // 继续从备份队列中取出任务，进入下一次循环
                            task = backupQueue.popTask();
                            continue;
                        }
                    }

                    Task originTask = JSON.parseObject(JSON.toJSONString(task), Task.class);// 保留原任务数据，用于删除该任务

                    if (status.getRetry() < this.getRetryTimes()) {
                        // 重新放入任务队列
                        // 更新状态标记为retry
                        status.setState(Constant.RETRY);
                        // 更新重试次数retry + 1
                        status.setRetry(status.getRetry() + 1);
                        task.setTaskStatus(status);
                        // 放入任务队列的队首，优先处理
                        taskQueue.pushTaskToHeader(task);
                    } else {
                        if (pipeline != null) {
                            pipeline.process(taskQueue, task);// 彻底失败任务的处理
                        }
                    }

                    // 删除备份队列中的该任务
                    backupQueue.finishTask(originTask);
                }
                // 继续从备份队列中取出任务，进入下一次循环
                task = backupQueue.popTask();
            }

        } catch (Throwable e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 构建器，用于设置初始化参数，执行初始化操作
     */
    public static class Builder {

        /**
         * 备份队列名称
         */
        private String backUpQueueName;


        /**
         * redis连接
         */
        private RedisTemplate<String, String> redisTemplate = null;


        /**
         * 任务超时重试次数，默认3次
         */
        private int retryTimes;

        /**
         * 失败任务（重试三次失败）的处理方式
         */
        private Pipeline pipeline;

        /**
         * 任务的存活超时时间。注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间。单位：ms
         * <p>
         * 该值只针对安全队列起作用
         * <p>
         * 不设置默认为 Long.MAX_VALUE
         */
        private long aliveTimeout;

        /**
         * 任务执行的超时时间（一次执行），单位：ms
         * <p>
         * 该值只针对安全队列起作用
         * <p>
         * 不设置默认为 Long.MAX_VALUE
         */
        private long protectedTimeout;

        /**
         * 健康检查
         */
        private AliveDetectHandler aliveDetectHandler;

        /**
         * 创建Builder对象
         * <p>
         *
         * @param redisTemplate   redis连接
         * @param backUpQueueName 备份队列名称
         */
        public Builder(RedisTemplate<String, String> redisTemplate, String backUpQueueName) {
            Assert.notNull(redisTemplate, "Param redisTemplate can't null");
            Assert.notNull(backUpQueueName, "Param backUpQueueName can't null");

            this.retryTimes = 3;
            this.aliveTimeout = Long.MAX_VALUE;
            this.protectedTimeout = Long.MAX_VALUE;
            this.redisTemplate = redisTemplate;
            this.backUpQueueName = backUpQueueName;
        }

        /**
         * 设置失败任务（重试三次失败）的处理方式
         *
         * @param pipeline 失败任务（重试三次失败）的处理方式
         * @return 返回Builder
         */
        public Builder setPipeline(Pipeline pipeline) {
            this.pipeline = pipeline;
            return this;
        }

        /**
         * 任务超时重试次数，默认3次
         *
         * @param retryTimes 任务超时重试次数
         * @return 返回Builder
         */
        public Builder setRetryTimes(int retryTimes) {
            Assert.greaterThanEquals(retryTimes, 0, "Param retryTimes is negative");
            this.retryTimes = retryTimes;
            return this;
        }

        /**
         * 设置任务的存活超时时间。单位：ms
         * <p>
         * 注意，该时间是任务从创建({@code new Task(...)})到销毁的总时间
         * <p>
         * 传0 则采用默认值： Long.MAX_VALUE
         * <p>
         * 该值只针对安全队列起作用
         *
         * @param aliveTimeout 任务的存活时间
         * @return 返回Builder
         */
        public Builder setAliveTimeout(long aliveTimeout) {
            Assert.greaterThanEquals(aliveTimeout, 0, "Param aliveTimeout is negative");
            if (aliveTimeout == 0) {
                aliveTimeout = Long.MAX_VALUE;
            }
            this.aliveTimeout = aliveTimeout;
            return this;
        }

        /**
         * 任务执行的超时时间（一次执行）。单位：ms
         * <p>
         * 传0 则采用默认值： Long.MAX_VALUE
         * <p>
         * 该值只针对安全队列起作用
         * <p>
         * 不设置默认为 Long.MAX_VALUE
         *
         * @param protectedTimeout 任务执行的超时时间
         * @return 返回Builder
         */
        public Builder setProtectedTimeout(long protectedTimeout) {
            Assert.greaterThanEquals(protectedTimeout, 0, "Param protectedTimeout is negative");
            if (protectedTimeout == 0) {
                protectedTimeout = Long.MAX_VALUE;
            }
            this.protectedTimeout = protectedTimeout;
            return this;
        }

        /**
         * 注册健康检查
         *
         * @param aliveDetectHandler 健康检测实现
         * @return 返回Builder
         */
        public Builder registerAliveDetectHandler(AliveDetectHandler aliveDetectHandler) {
            this.aliveDetectHandler = aliveDetectHandler;
            return this;
        }

        public BackupQueueMonitor build() {
            BackupQueueMonitor queueMonitor = new BackupQueueMonitor();
            queueMonitor.redisTemplate = this.redisTemplate;
            queueMonitor.backUpQueueName = this.backUpQueueName;
            queueMonitor.retryTimes = this.retryTimes;
            queueMonitor.pipeline = this.pipeline;
            queueMonitor.aliveTimeout = this.aliveTimeout;
            queueMonitor.protectedTimeout = this.protectedTimeout;
            queueMonitor.aliveDetectHandler = this.aliveDetectHandler;
            queueMonitor.backupQueue = new RedisBackupQueue(queueMonitor);// 备份队列
            return queueMonitor;
        }

    }


}
