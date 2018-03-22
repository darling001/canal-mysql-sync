package com.wanjun.canalsync.service.impl;

import com.wanjun.canalsync.annotation.DS;
import com.wanjun.canalsync.dao.ItemLineDao;
import com.wanjun.canalsync.service.ItemAggService;
import com.wanjun.canalsync.service.ItemLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-22
 */
@Service
public class ItemLineServiceImpl implements ItemLineService {
    @Autowired
    private ItemLineDao itemLineDao;
    @DS("gms1")
    @Override
    public List<Map<String, Object>> getItemLineMap(String itemId) {
        return null;
    }
}
