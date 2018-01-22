package com.wanjun.canalsync;

import com.google.common.collect.Maps;
import com.wanjun.canalsync.service.RedisService;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-19
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisServiceImplTest {

    @Resource
    private RedisService redisService;

    @Test
    public void insertById() throws Exception {
        Map<String,Object> target = Maps.newHashMap();
        target.put("id",1);
        target.put("bookType",1);
        target.put("bookname","javacode");
        redisService.hset("wanjun.door","1",target);
    }

    @Test
    public void testHHasKey() {
        Map<String,Object> value = redisService.hget("wanjun.bookType","1",Map.class);
        System.out.println("value = " + value);
    }
}
