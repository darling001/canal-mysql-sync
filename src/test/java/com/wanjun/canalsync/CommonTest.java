package com.wanjun.canalsync;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.wanjun.canalsync.model.AggregationModel;
import com.wanjun.canalsync.service.MappingService;
import com.wanjun.canalsync.util.JSONUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-19
 */
public class CommonTest {
    @Test
    public void testRandom() {
        for(int i=0;i<10;i++) {
            System.out.println(RandomUtils.nextInt(1,3));

        }
    }
    @Test
    public void testIdentifiedMap() {
        ArrayListMultimap<String,String> multiMap=ArrayListMultimap.create();
        multiMap.put("Foo","1");
        multiMap.put("boo","2");
        multiMap.put("coo","4");
        multiMap.put("coo","3");
        Map<String, Collection<String>> stringCollectionMap = multiMap.asMap();
        System.out.println(multiMap);

    }
    @Test
    public void testPath() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println("path = " + path);
    }

    @Test
    public void testFile() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        System.out.println("path = " + path);
        FileUtils.touch(new File(path + "c.txt"));
    }


    @Test
    public void testLombok() {
        AggregationModel aggregationModel = new AggregationModel();
        System.out.println(aggregationModel.getDatabaseName());
    }

    @Test
    public void testCommon() throws Exception {
        System.out.println(DateTime.parse("2017-09-01 16:21:17", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).toDate());
//        long i = 7989501;
        long maxPK = 1000000;

        for (long j = 0; j < maxPK; j++) {
            System.out.println(String.format("当前同步pk=%s，总共total=%s，进度=%s%%", j, maxPK, new BigDecimal(j * 100).divide(new BigDecimal(maxPK), 2, BigDecimal.ROUND_HALF_UP)));
        }
    }

    @Test
    public void testThread() throws Exception {
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("11");
                return t;
            }
        });
        for (int i = 0; i < 5; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        System.out.println(Thread.currentThread().getName() + " is run");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        Thread.sleep(10000);
        executorService.shutdown();
    }
    @Test
    public void testJson() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("createTime",new Timestamp(System.currentTimeMillis()));
        map.put("userId",100000);
        map.put("userName","xxxx");
        System.out.println("map = " + JSONUtil.toJson(map));
    }
}
