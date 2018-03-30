package com.wanjun.canalsync.dao;

import com.wanjun.canalsync.model.TagInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-30
 */
@Repository
public interface TagInfoDao {

    public List<TagInfo> queryTagInfoTreeList(Integer pid);


    public List<TagInfo> queryTagInfoChildList(@Param("childTagInfoIds") List<Integer> childTagInfoIds);
}
