package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.dao.CategoryDao;
import com.wanjun.canalsync.dao.ItemLineDao;
import com.wanjun.canalsync.dao.ItemPictureDao;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.ItemAggService;
import com.wanjun.canalsync.util.SelectType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-05
 */
@Service
@Schema("gms")
@SuppressWarnings("all")
public class ItemAggServiceImpl implements ItemAggService {

    private static final Logger logger = LoggerFactory.getLogger(ItemAggServiceImpl.class);

    @Autowired
    private BaseDao baseDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ItemLineDao itemLineDao;

    @Autowired
    private ItemPictureDao itemPictureDao;

    @Autowired
    private ElasticsearchService elasticsearchService;


    @Override
    @Table(value = "item", event = {CanalEntry.EventType.INSERT})
    public void aggAddItem(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();
        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            Map<String, Object> resultMap = null;
            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                resultMap = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                resultMap = categoryDao.selectCategoryList(colValue.toString());
            }

            if (resultMap != null && !resultMap.isEmpty()) {
                map.put(aggConfig[1], resultMap);
            }

        });

        elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), map);
    }

    @Override
    @Table(value = "item", event = {CanalEntry.EventType.UPDATE})
    public void aggUpdateItem(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();
        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            Map<String, Object> resultMap = null;
            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                resultMap = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                resultMap = categoryDao.selectCategoryList(colValue.toString());
            }

            if (resultMap != null && !resultMap.isEmpty()) {
                map.put(aggConfig[1], resultMap);
            }

        });
        Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, map.get("ITEM_ID").toString(), null);
        esResult.putAll(map);
        elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), esResult);
    }

    @Table(value = "item_line", event = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    @Override
    public void aggItemLine(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();

        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();

        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            if (aggConfig.length == 0) {
                return;
            }
            List<Map<String, Object>> result = itemLineDao.getItemLineMap(colValue.toString());
            if (result != null && !result.isEmpty()) {
                //map.put(aggConfig[1], result);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, colValue.toString(), null);
                if (esResult != null && !esResult.isEmpty()) {
                    esResult.put(aggConfig[1], result);
                }
                elasticsearchService.insertById(index, aggType, colValue.toString(), esResult);
            }
        });

    }

    @Table(value = "picture_list", event = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    @Override
    public void aggItemPicture(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();

        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();

        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            if (aggConfig.length == 0) {
                return;
            }
            List<Map<String, Object>> result = itemPictureDao.getItemPictureMap(colValue.toString(), "sku");
            if (result != null && !result.isEmpty()) {
                //map.put(aggConfig[1], result);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, colValue.toString(), null);
                if (esResult != null && !esResult.isEmpty()) {
                    esResult.put(aggConfig[1], result);
                }
                elasticsearchService.insertById(index, aggType, colValue.toString(), esResult);
            }
        });
    }

    @Table(value = "item_price", event = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    @Override
    public void aggItemPrice(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();

        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            if (aggConfig.length == 0) {
                return;
            }
            Map<String, Object> resultMap = null;
            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                resultMap = baseDao.selectByPK(key, colValue, aggConfig[0], aggConfig[1]);
                Object itemId = resultMap.get(aggConfig[2]);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, itemId.toString(), null);
                Object obj = esResult.get("cmc_item_line");
                if (obj instanceof List) {
                    List<Map<String, Object>> lineMap = (List<Map<String, Object>>) obj;
                    int step = 0;
                    for (int i = 0; i < lineMap.size(); i++) {
                        Map<String, Object> item = lineMap.get(i);
                        Set<String> keys = item.keySet();
                        for (String mapKey : keys) {
                            if (mapKey.equals(key)) {
                                step = i;
                                break;
                            }
                        }
                    }
                    //找到了添加
                    if(step != 0) {
                        lineMap.get(step).putAll(map);
                    }
                }
                elasticsearchService.insertById(index, aggType, itemId.toString(), esResult);
            }
        });
    }


}
