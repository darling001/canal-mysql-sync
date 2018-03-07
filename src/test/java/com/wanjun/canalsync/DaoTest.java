package com.wanjun.canalsync;

import com.wanjun.canalsync.dao.ItemLineDao;
import com.wanjun.canalsync.util.JSONUtil;
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

    @Test
    public void testItemLineDao() {
        List<Map<String,Object>>itemLineMap = itemLineDao.getItemLineMap("a0ef6aa8aed9cbcede931ba9491fb6d5");
        System.out.println(JSONUtil.toJson(itemLineMap));
    }
}
