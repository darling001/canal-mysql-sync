package com.wanjun.canalsync.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-02-02
 */
@Component
public class CanalTaskScheduling implements Runnable {

    @Override
    @Scheduled(fixedDelay = 100)
    public void run() {

    }
}
