package com.wanjun.canalsync.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-06
 */
@Repository
public interface ItemDescDao {
    public List<Map<String,Object>> getItemDescMap(@Param("itemId") String itemId);
}
