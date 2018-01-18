package com.wanjun.canalsync.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2017-12-10
 */
public class DeleteCanalEvent extends CanalEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DeleteCanalEvent(Entry source) {
        super(source);
    }
}
