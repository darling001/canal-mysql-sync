package com.wanjun.canalsync;

import com.wanjun.canalsync.model.EsPage;
import com.wanjun.canalsync.service.ElasticsearchService;
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
    public void testCreateIndex() {
        boolean value = elasticsearchService.createIndex("test");
        Assert.assertTrue(value);
    }

    @Test
    public void testDeleteIndex() {
        boolean result = elasticsearchService.deleteIndex("wanjun");
        Assert.assertTrue(result);
    }

    @Test
    public void isIndexExist() {
        boolean result = elasticsearchService.isIndexExist("test");
        Assert.assertTrue(result);
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
        EsPage esPage = elasticsearchService.searchDataPage("wanjun", "emp", 1, 100, 0, 0, null, null, false, "gender", "gender=M");
        System.out.println("esPage = " + esPage);

       // System.out.println("list = " + list);
        /*System.out.println("list1 = " + list1);
        System.out.println("list2 = " + list2);*/

    }

}
