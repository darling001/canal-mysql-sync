package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.dao.BaseDao;
import com.wanjun.canalsync.dao.CategoryDao;
import com.wanjun.canalsync.dao.ItemLineDao;
import com.wanjun.canalsync.dao.ItemPictureDao;
import com.wanjun.canalsync.model.Category;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.service.ItemAggService;
import com.wanjun.canalsync.util.SelectType;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.recycler.Recycler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                Map<String, Object> resultMap = resultMap = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
                map.put(aggConfig[1], resultMap);

            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                Category categoryTree = categoryDao.selectCategoryList(colValue.toString());
                List<Map<String,Object>> categoryMapList  = Lists.newArrayList();
                getChildCategory(categoryMapList,categoryTree);
                map.put(aggConfig[1],categoryMapList);
            }


        });

        elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), map);
    }

    private void getChildCategory(List<Map<String,Object>> categoryMapList, Category treeCategory) {
        if (treeCategory == null) {
            return;
        }
        Map<String,Object> categoryMap =Maps.newHashMap();
        categoryMap.put("CATEGORY_ID",treeCategory.getCategoryId());
        categoryMap.put("CATEGORY_CODE",treeCategory.getCategoryCode());
        categoryMap.put("CATEGORY_NAME",treeCategory.getCategoryName());
        categoryMap.put("CATEGORY_LEVEL",treeCategory.getCategoryLevel());
        categoryMapList.add(categoryMap);
        getChildCategory(categoryMapList, treeCategory.getChild());

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
            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                Map<String, Object> resultMap  = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
                map.put(aggConfig[1], resultMap);
            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                Category categoryTree = categoryDao.selectCategoryList(colValue.toString());
                List<Map<String,Object>> categoryMapList  = Lists.newArrayList();
                getChildCategory(categoryMapList,categoryTree);
                map.put(aggConfig[1],categoryMapList);
            }


        });
        Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, map.get("ITEM_ID").toString(), null);
        if(esResult == null) {
            elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), map);
        }else {
            esResult.putAll(map);
            elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), esResult);
        }
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
                List<Map<String, Object>> result = itemLineDao.getItemLineMap(itemId.toString());
                if (result != null && !result.isEmpty()) {
                    Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType,itemId.toString(), null);
                    if (esResult != null && !esResult.isEmpty()) {
                        esResult.put(aggConfig[1], result);
                    }
                    elasticsearchService.insertById(index, aggType, itemId.toString(), esResult);
                }
            }
        });

    }

    private void aggCommon(Map<String,Object> map ,IndexTypeModel indexTypeModel) {
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
            String matchStr = String.format("%s=%s", aggConfig[2], colValue);
            List<Map<String, Object>> esResult = elasticsearchService.searchListData(index, aggType, null, null, null, true, matchStr);
            Map<String, Map<String, Object>> idDataMap = null;
            if(esResult != null && !esResult.isEmpty()) {
                idDataMap = Maps.newHashMap();
            }
            for(int i=0;i<esResult.size();i++) {
                Map<String,Object> elementMap  = esResult.get(i);
                String itemId = elementMap.get("ITEM_ID").toString();
                elementMap.put(aggConfig[1],map);
                idDataMap.put(itemId,elementMap);

            }

            elasticsearchService.batchInsertById(index,aggType,idDataMap);
        });
    }

    @Override
    @Table(value = "brand", event = {CanalEntry.EventType.UPDATE})
    public void aggBrand(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        this.aggCommon(map,indexTypeModel);
    }

    @Override
    @Table(value = "spu" ,event = {CanalEntry.EventType.UPDATE})
    public void aggSPU(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        this.aggCommon(map,indexTypeModel);
    }

    @Override
    @Table(value = "category",event = {CanalEntry.EventType.UPDATE})
    public void aggCategory(Map<String, Object> map, IndexTypeModel indexTypeModel) {
       // this.aggCommon(map,indexTypeModel);
    }


}
