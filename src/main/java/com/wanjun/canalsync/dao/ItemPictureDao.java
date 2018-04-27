package com.wanjun.canalsync.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchengli on 2018/3/7
 */
@Repository
public interface ItemPictureDao {

    public List<Map<String,Object>> getItemPictureMap(@Param("billNo") String billNo,@Param("billTypes") List<String> billTypes);
}
