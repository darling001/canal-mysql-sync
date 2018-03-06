package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.dao.CategoryDao;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.ItemAggService;
import com.wanjun.canalsync.util.SelectType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-05
 */
@Service
@Schema("gms")
public class ItemAggServiceImpl implements ItemAggService {

    private static final Logger logger = LoggerFactory.getLogger(ItemAggServiceImpl.class);

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ElasticsearchService elasticsearchService;


    @Override
    @Table(value = "item", event = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    public void save(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();
        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] databaseAndTable = StringUtils.split(value, ".");
            Map<String, Object> resultMap = null;
            String selectType = databaseAndTable[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                resultMap = baseDao.selectByPK(databaseAndTable[2], colValue, databaseAndTable[0], databaseAndTable[1]);
            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                resultMap = categoryDao.selectCategoryList(colValue.toString());
            }

            if (resultMap == null) {
                logger.error("key:{},colValue:{},database:{},table:{},result{}", databaseAndTable[2], colValue, databaseAndTable[0], databaseAndTable[1], resultMap);
            } else if (resultMap != null) {
                map.put(databaseAndTable[1], resultMap);
            }

        });

        elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").

                toString(), map);
    }
}
