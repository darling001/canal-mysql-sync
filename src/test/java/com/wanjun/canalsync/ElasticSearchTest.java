package com.wanjun.canalsync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.model.EsPage;
import com.wanjun.canalsync.service.ElasticsearchService;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
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
 * @date 2018-01-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchTest {
    @Resource
    private ElasticsearchService elasticsearchService;
    @Test
    public void testInsertById() {
        for(int i=2;i<10000;i++) {
            long num1 = RandomUtils.nextLong(1,10000);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", i);
            map.put("goodName", "无人机" + num1);
            map.put("price", num1);
            map.put("brandName", "大疆");
            Map<String, Object> properties1 = Maps.newHashMap();
            properties1.put("value", Lists.newArrayList("v1","v2"));
            properties1.put("name", "属性1");
            Map<String, Object> properties2 = Maps.newHashMap();
            properties2.put("value", Lists.newArrayList("v3","v4"));
            properties2.put("name", "属性2");

            Map<String, Object> properties3 = Maps.newHashMap();
            properties3.put("value",  Lists.newArrayList("v5","v6"));
            properties3.put("name", "属性3");
            List<Map<String, Object>> list = Lists.newArrayList();
            list.add(properties1);
            list.add(properties2);
            list.add(properties3);
            map.put("properties", list);
            elasticsearchService.insertById("test", "test", i+"", map);
        }
    }

    @Test
    public void testCreateIndex() {
        boolean value = elasticsearchService.createIndex("test");
        Assert.assertTrue(value);
    }

    @Test
    public void testDeleteIndex() {
        boolean result = elasticsearchService.deleteIndex("gms");
        Assert.assertTrue(result);
    }

    @Test
    public void isIndexExist() {
        boolean result = elasticsearchService.isIndexExist("test");
        Assert.assertTrue(result);
    }
    @Test
    public void testSearchData() {
        Map<String, Object> stringObjectMap = elasticsearchService.searchDataById("test", "test", String.valueOf(10), null);
        System.out.println(stringObjectMap);
    }

    @Test
    public void testSearchDataById() {
        //默认全部的字段
        Map<String, Object> stringObjectMap = elasticsearchService.searchDataById("wanjun", "emp", String.valueOf(1), null);

        Map<String, Object> stringObjectMap1 = elasticsearchService.searchDataById("wanjun", "emp", String.valueOf(1), "dept_id,gender,emp_name");

        System.out.println("stringObjectMap = " + stringObjectMap);
        System.out.println("stringObjectMap1 = " + stringObjectMap1);
    }

    @Test
    public void testSearchListData() {
        //List<Map<String, Object>> list = elasticsearchService.searchListData("wanjun", "emp", 1000, null, "gender=M");
        // List<Map<String, Object>> list1 = elasticsearchService.searchListData("wanjun", "emp", 1000, null, "gender=M");
        // List<Map<String, Object>> list2 = elasticsearchService.searchListData("wanjun", "emp", 100, null, null, false, "gender", "gender=M");
        EsPage esPage = elasticsearchService.searchDataPage("gms", "item_agg", 1, 100, 0, 0, null, null, false, "gender", "item_name=铜管,item_shortname=");
        System.out.println("esPage = " + esPage);

       // System.out.println("list = " + list);
        /*System.out.println("list1 = " + list1);
        System.out.println("list2 = " + list2);*/

    }

}
