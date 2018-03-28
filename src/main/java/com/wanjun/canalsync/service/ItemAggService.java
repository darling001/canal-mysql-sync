package com.wanjun.canalsync.service;

import com.wanjun.canalsync.model.IndexTypeModel;

import java.util.Map;

/**
 * Created by wangchengli on 2018/3/5
 * SKU聚合接口
 */
public interface ItemAggService {

    public void aggAddItem(Map<String,Object> map, IndexTypeModel indexTypeModel);

    public void aggUpdateItem(Map<String,Object> map, IndexTypeModel indexTypeModel);

    public void deleteAggItem(Map<String,String> map ,IndexTypeModel indexTypeModel) ;

    public void aggItemLine(Map<String, Object> map, IndexTypeModel indexTypeModel) ;

    public void aggItemPicture(Map<String, Object> map, IndexTypeModel indexTypeModel);

    public void aggItemPrice(Map<String,Object> map,IndexTypeModel indexTypeModel);


    public void aggBrand(Map<String,Object> map ,IndexTypeModel indexTypeModel);

    public void aggSPU(Map<String,Object> map,IndexTypeModel indexTypeModel);

    public void aggCategory(Map<String,Object> map,IndexTypeModel indexTypeModel);
}
