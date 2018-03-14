package com.wanjun.canalsync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.model.EsPage;
import com.wanjun.canalsync.model.SpecAttribute;
import com.wanjun.canalsync.service.ElasticsearchService;
import com.wanjun.canalsync.util.JSONUtil;
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
    public void testParseJson() {
        String json = "[\n" +
                "  {\n" +
                "    \"PRICE_FLAG\":\"1\",\n" +
                "    \"ATTRIBUTE_NAME\":\"颜色\",\n" +
                "    \"ATTRIBUTE_VALUE\":\"红色\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"PRICE_FLAG\":\"0\",\n" +
                "    \"ATTRIBUTE_NAME\":\"尺寸\",\n" +
                "    \"ATTRIBUTE_VALUE\":\"红色\"\n" +
                "  }\n" +
                "]";
        List<SpecAttribute> specAttributes = JSONUtil.toList(json, SpecAttribute.class);
        System.out.println(JSONUtil.toJson(specAttributes));
    }

    @Test
    public void testInsertById() {
       Map<String,Object> map = Maps.newHashMap();
       map.put("aaa",1);
       map.put("bbb","bbb");
       map.put("ccc",2.14);
       Map<String,Map<String,Object>> esMap = Maps.newHashMap();
       esMap.put("2",map);
       elasticsearchService.batchInsertById("test","test",esMap);
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
        List<Map<String, Object>> list = elasticsearchService.searchListData("gms", "item_agg", null, null, null, true, "TRADEMARK=06e9f408d4a5f353c5f8da925bad341f");
        System.out.println(JSONUtil.toJson(list));
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
