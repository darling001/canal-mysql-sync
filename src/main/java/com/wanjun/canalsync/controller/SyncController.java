package com.wanjun.canalsync.controller;

import com.wanjun.canalsync.model.request.SyncByTableRequest;
import com.wanjun.canalsync.model.response.Response;
import com.wanjun.canalsync.service.SyncService;
import com.wanjun.canalsync.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wangchengli
 * @version 1.0
 * @since 2018-01-15
 */
@Controller
@RequestMapping("/sync")
public class SyncController {
    private static final Logger logger = LoggerFactory.getLogger(SyncController.class);

    @Resource
    private SyncService syncService;

    /**
     * 通过库名和表名全量同步数据
     *
     * @param request 请求参数
     */
    @RequestMapping("/byTable")
    @ResponseBody
    public String syncTable(@Validated SyncByTableRequest request) {
        logger.debug("request_info: " + JsonUtil.toJson(request));
        String response = Response.success(syncService.syncByTable(request)).toString();
        logger.debug("response_info: " + JsonUtil.toJson(request));
        return response;
    }
}
