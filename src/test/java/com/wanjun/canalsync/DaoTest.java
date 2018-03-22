package com.wanjun.canalsync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import com.wanjun.canalsync.dao.CategoryDao;
import com.wanjun.canalsync.dao.ItemLineDao;
import com.wanjun.canalsync.model.Category;
import com.wanjun.canalsync.util.JSONUtil;
import org.elasticsearch.index.mapper.SourceToParse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {
    @Resource
    private ItemLineDao itemLineDao;
    @Resource
    private CategoryDao categoryDao;

    @Test
    public void testItemLineDao() {
        List<Map<String, Object>> itemLineMap = itemLineDao.getItemLineMap("a0ef6aa8aed9cbcede931ba9491fb6d5");
        System.out.println(JSONUtil.toJson(itemLineMap));
    }

    @Test
    public void testCategoryDao() {
        Category category = categoryDao.selectCategoryList("06fdc70bf9b461443d0c7665badd7091");
        List<Map> categoryList = Lists.newArrayList();
        getChildCategory(categoryList, category);
        System.out.println("categoryList = " + categoryList);
    }


    private void getChildCategory(List<Map> categoryMapList, Category treeCategory) {
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
}