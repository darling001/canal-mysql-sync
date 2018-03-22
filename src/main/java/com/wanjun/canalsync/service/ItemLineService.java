package com.wanjun.canalsync.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-22
 */
public interface ItemLineService {

    public List<Map<String,Object>> getItemLineMap(String itemId);
}
