package com.wanjun.canalsync.service.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.annotation.Schema;
import com.wanjun.canalsync.annotation.Table;
import com.wanjun.canalsync.client.DynamicDataSourceContextHolder;
import com.wanjun.canalsync.dao.*;
import com.wanjun.canalsync.model.Category;
import com.wanjun.canalsync.model.IndexTypeModel;
import com.wanjun.canalsync.model.SpecAttribute;
import com.wanjun.canalsync.service.*;
import com.wanjun.canalsync.util.Constants;
import com.wanjun.canalsync.util.JSONUtil;
import com.wanjun.canalsync.util.SelectType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private ItemDescDao itemDescDao;

    @Autowired
    private ElasticsearchService elasticsearchService;

    private List<Map<String, Object>> parseSpecContents(String json) {
        List<Map<String, Object>> list = Lists.newArrayList();
        List<SpecAttribute> specAttributeList = null;
        if (StringUtils.isEmpty(json)) {
            return list;
        }
        try {
            specAttributeList = JSONUtil.toList(json, SpecAttribute.class);
        } catch (Exception e) {
            logger.error("ItemAggService->parseSpecContents error! param json {}", json, e);
            return list;
        }
        for (SpecAttribute specAttribute : specAttributeList) {
            Map<String, Object> map = Maps.newHashMap();
            String priceFlag = StringUtils.trimToNull(specAttribute.getPriceFlag());
            String attributeName = StringUtils.trimToNull(specAttribute.getAttributeName());
            String attributeValue  = StringUtils.trimToNull(specAttribute.getAttributeValue());
            String orderSort  = StringUtils.trimToNull(specAttribute.getOrderSort());
            if(StringUtils.isNotEmpty(priceFlag)) {
                map.put("PRICE_FLAG", priceFlag);
            }
            if(StringUtils.isNotEmpty(attributeName)) {
                map.put("ATTRIBUTE_NAME", attributeName);
            }
            if(StringUtils.isNotEmpty(attributeValue)) {
                map.put("ATTRIBUTE_VALUE", attributeValue);
            }
            if(StringUtils.isNotEmpty(orderSort)) {
                map.put("ORDER_SORT",orderSort);
            }
            list.add(map);
        }
        return list;
    }

    @Override
    @Table(value = "item", event = {CanalEntry.EventType.DELETE})
    public void deleteAggItem(Map<String, String> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();
        Object itemId = map.get("ITEM_ID");
        if (Objects.isNull(itemId)) {
            return;
        }
        elasticsearchService.deleteById(index, aggType, itemId.toString());

    }

    @Override
    @Table(value = "item", event = {CanalEntry.EventType.INSERT})
    public void aggAddItem(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        //聚合数据es类型
        String aggType = indexTypeModel.getAggType();
        //索引
        String index = indexTypeModel.getIndex();

        Object json = map.get("SPEC_CONTENTS");
        if (json != null) {
            List<Map<String, Object>> specAttributeList = parseSpecContents(json.toString());
            map.put("SPEC_CONTENTS", specAttributeList);

        }
        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");

            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                Map<String, Object> resultMap = resultMap = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
                Map<String, Object> mapJson = Maps.newHashMap();
                if (resultMap != null && resultMap.size() > 0) {
                    mapJson = JSONUtil.toMap(JSONUtil.toJson(resultMap));
                }
                map.put(aggConfig[1], mapJson);

            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                Category categoryTree = null;
                if (colValue != null) {
                    categoryTree = categoryDao.selectCategoryList(colValue.toString());
                }
                List<Map<String, Object>> categoryMapList = Lists.newArrayList();
                getChildCategory(categoryMapList, categoryTree);
                map.put(aggConfig[1], categoryMapList);
            }


        });

        elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), map);
    }

    private void getChildCategory(List<Map<String, Object>> categoryMapList, Category treeCategory) {
        if (treeCategory == null) {
            return;
        }
        Map<String, Object> categoryMap = Maps.newHashMap();
        categoryMap.put("CATEGORY_ID", treeCategory.getCategoryId());
        categoryMap.put("CATEGORY_CODE", treeCategory.getCategoryCode());
        categoryMap.put("CATEGORY_NAME", treeCategory.getCategoryName());
        categoryMap.put("CATEGORY_LEVEL", treeCategory.getCategoryLevel());
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

        Object json = map.get("SPEC_CONTENTS");
        if (json != null) {
            List<Map<String, Object>> specAttributeList = parseSpecContents(json.toString());
            map.put("SPEC_CONTENTS", specAttributeList);

        }
        Map<String, String> pkMappingTableMap = indexTypeModel.getPkMappingTableMap();
        pkMappingTableMap.forEach((key, value) -> {
            Object colValue = map.get(key);
            String[] aggConfig = StringUtils.split(value, ".");
            String selectType = aggConfig[3];
            if (StringUtils.equals(selectType, SelectType.PK.getType())) {
                Map<String, Object> resultMap = baseDao.selectByPK(aggConfig[2], colValue, aggConfig[0], aggConfig[1]);
                Map<String, Object> mapJson = Maps.newHashMap();
                if (resultMap != null && resultMap.size() > 0) {
                    mapJson = JSONUtil.toMap(JSONUtil.toJson(resultMap));
                }
                map.put(aggConfig[1], mapJson);
            } else if (StringUtils.equals(selectType, SelectType.SELF_JOIN.getType())) {
                Category categoryTree = null;
                if (colValue != null) {
                    categoryTree = categoryDao.selectCategoryList(colValue.toString());
                }
                List<Map<String, Object>> categoryMapList = Lists.newArrayList();
                getChildCategory(categoryMapList, categoryTree);
                map.put(aggConfig[1], categoryMapList);
            }


        });
        Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, map.get("ITEM_ID").toString(), null);
        if (esResult == null) {
            elasticsearchService.insertById(index, aggType, map.get("ITEM_ID").toString(), map);
        } else {
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
            if (colValue == null) {
                logger.error("aggItemLine colValue is null ,colValue {},EventType {}", colValue, CanalEntry.EventType.UPDATE);
                return;
            }
            List<Map<String, Object>> result = itemLineDao.getItemLineMap(colValue.toString());
            List jsonList = Lists.newArrayList();
            if (result != null && !result.isEmpty()) {
                jsonList = JSONUtil.toList(JSONUtil.toJson(result), Map.class);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, colValue.toString(), null);
                if (esResult != null && !esResult.isEmpty()) {
                    esResult.put(aggConfig[1], jsonList);
                    elasticsearchService.insertById(index, aggType, colValue.toString(), esResult);
                }
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
            if (colValue == null) {
                logger.error("aggItemPicture colValue is null ,colValue {},EventType {}", colValue, CanalEntry.EventType.UPDATE);
                return;
            }
            List<Map<String, Object>> result = itemPictureDao.getItemPictureMap(colValue.toString(), Arrays.asList("cmcItem","cmcItemDesc"));
            List jsonList = Lists.newArrayList();
            if (result != null && !result.isEmpty()) {
                //为了解决Elasticsearch中，Date格式不匹配
                jsonList = JSONUtil.toList(JSONUtil.toJson(result), Map.class);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, colValue.toString(), null);
                if (esResult != null && !esResult.isEmpty()) {
                    esResult.put(aggConfig[1], jsonList);
                    elasticsearchService.insertById(index, aggType, colValue.toString(), esResult);
                }
            }
        });
    }
    @Table(value = "item_desc", event = {CanalEntry.EventType.INSERT, CanalEntry.EventType.UPDATE})
    @Override
    public void aggItemDesc(Map<String, Object> map, IndexTypeModel indexTypeModel) {
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
            if (colValue == null) {
                logger.error("aggItemDesc colValue is null ,colValue {},EventType {}", colValue, CanalEntry.EventType.UPDATE);
                return;
            }
            List<Map<String, Object>> result = itemDescDao.getItemDescMap(colValue.toString());
            List jsonList = Lists.newArrayList();
            if (result != null && !result.isEmpty()) {
                //为了解决Elasticsearch中，Date格式不匹配
                jsonList = JSONUtil.toList(JSONUtil.toJson(result), Map.class);
                Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, colValue.toString(), null);
                if (esResult != null && !esResult.isEmpty()) {
                    esResult.put(aggConfig[1], jsonList);
                    elasticsearchService.insertById(index, aggType, colValue.toString(), esResult);
                }
            }
        });
    }
        //聚合数据es类型
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
            if (colValue == null) {
                logger.error("aggItemPrice colValue is null ,colValue {},EventType {}", colValue, CanalEntry.EventType.UPDATE);
                return;
            }
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
                List jsonList = Lists.newArrayList();
                if (result != null && !result.isEmpty()) {
                    jsonList = JSONUtil.toList(JSONUtil.toJson(result), Map.class);
                    Map<String, Object> esResult = elasticsearchService.searchDataById(index, aggType, itemId.toString(), null);
                    if (esResult != null && !esResult.isEmpty()) {
                        esResult.put(aggConfig[1], jsonList);
                        elasticsearchService.insertById(index, aggType, itemId.toString(), esResult);
                    }
                }
            }
        });

    }

    private void aggCommon(Map<String, Object> map, IndexTypeModel indexTypeModel) {
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
            if (colValue == null) {
                logger.error("aggCommon colValue is null ,colValue {}", colValue);
                return;
            }
            String matchStr = String.format("%s=%s", aggConfig[2], colValue);
            List<Map<String, Object>> esResult = elasticsearchService.searchListData(index, aggType, null, null, null, true, matchStr);
            Map<String, Map<String, Object>> idDataMap = null;
            if (esResult != null && !esResult.isEmpty()) {
                idDataMap = Maps.newHashMap();
            }
            for (int i = 0; i < esResult.size(); i++) {
                Map<String, Object> elementMap = esResult.get(i);
                String itemId = elementMap.get("ITEM_ID").toString();
                elementMap.put(aggConfig[1], map);
                idDataMap.put(itemId, elementMap);

            }
            if (idDataMap != null && !idDataMap.isEmpty()) {
                elasticsearchService.batchInsertById(index, aggType, idDataMap);
            }
        });
    }

    @Override
    @Table(value = "brand", event = {CanalEntry.EventType.UPDATE})
    public void aggBrand(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        this.aggCommon(map, indexTypeModel);
    }

    @Override
    @Table(value = "spu", event = {CanalEntry.EventType.UPDATE})
    public void aggSPU(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        this.aggCommon(map, indexTypeModel);
    }

    @Override
    @Table(value = "category", event = {CanalEntry.EventType.UPDATE})
    public void aggCategory(Map<String, Object> map, IndexTypeModel indexTypeModel) {
        String categoryLevel = map.get("CATEGORY_LEVEL").toString();
        if (StringUtils.isEmpty(categoryLevel) || !StringUtils.equals(Constants.CATEGORY_LEVEL, categoryLevel)) {
            logger.warn("当前修改的类目level{}", categoryLevel);
            return;
        }
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
            if (colValue == null) {
                logger.error("aggCategory colValue is null ,colValue {},EventType {}", colValue, CanalEntry.EventType.UPDATE);
                return;
            }
            String matchStr = String.format("%s=%s", aggConfig[2], colValue);
            List<Map<String, Object>> esResult = elasticsearchService.searchListData(index, aggType, null, null, null, true, matchStr);
            Map<String, Map<String, Object>> idDataMap = null;
            if (esResult != null && !esResult.isEmpty()) {
                idDataMap = Maps.newHashMap();
            }
            for (int i = 0; i < esResult.size(); i++) {
                Map<String, Object> elementMap = esResult.get(i);
                String itemId = elementMap.get("ITEM_ID").toString();
                Category categoryTree = categoryDao.selectCategoryList(colValue.toString());
                List<Map<String, Object>> categoryMapList = Lists.newArrayList();
                getChildCategory(categoryMapList, categoryTree);
                elementMap.put(aggConfig[1], categoryMapList);
                idDataMap.put(itemId, elementMap);
            }
            if (idDataMap != null && !idDataMap.isEmpty()) {
                elasticsearchService.batchInsertById(index, aggType, idDataMap);
            }
        });
    }


}
