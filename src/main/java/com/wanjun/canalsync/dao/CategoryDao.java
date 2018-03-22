package com.wanjun.canalsync.dao;


import com.wanjun.canalsync.annotation.DS;
import com.wanjun.canalsync.model.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-03-06
 */
@Repository
public interface CategoryDao {
    @DS("gms2")
    Category selectCategoryList(@Param("categoryId") String categoryId);

}
