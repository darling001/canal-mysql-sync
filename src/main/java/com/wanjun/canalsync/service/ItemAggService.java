package com.wanjun.canalsync.service;

import com.wanjun.canalsync.model.IndexTypeModel;

import java.util.Map;

/**
 * Created by wangchengli on 2018/3/5
 */
public interface ItemAggService {

    public void save(Map<String,Object> map, IndexTypeModel indexTypeModel);
}
