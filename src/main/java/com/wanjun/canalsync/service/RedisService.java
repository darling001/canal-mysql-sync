package com.wanjun.canalsync.service;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wangchengli
 * @version 1.0
 * @date 2018-01-19
 */
public interface RedisService {



    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */

    public boolean expire(String key, long time);

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */

    public long getExpire(String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */

    public boolean hasKey( String key) ;

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */

    @SuppressWarnings("unchecked")
    public void del(String... key) ;

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */

    public String get(String key) ;
    // ============================Object=============================

    /**
     * 对象缓存获取
     *
     * @param key 键
     * @return 值
     */

    public <T> T get(String key, Class<T> c) ;
    /**
     * 返回 list
     *
     * @param key
     * @param c
     * @return
     */
    public <T> List<T> getToList(String key, Class<T> c);

    /**
     * 对象缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */

    public boolean set(String key, Object value) ;

    /**
     * 对象缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */

    public boolean set( String key, Object value, long time) ;

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) ;
    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */

    public long decr( String key, long delta);

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) ;

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public <T> T hget(String key, String item, Class<T> c) ;

    /**
     * hgetList
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */

    public <T> List<T> hgetList(String key, String item, Class<T> c) ;

    /**
     * 获取整个哈希存储的值
     *
     * @param key
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> hmvget(String key, Class<T> c) ;

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) ;

    /**
     * HashSet
     *
     * @param <T>
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <T> boolean hmset(String key, Map<String, T> map) ;

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */

    public <T> boolean hmset(String key, Map<String, T> map, long time) ;

    /**
     * 获取hashKey对应的所有键值
     *
     * @param <T>
     * @param key 键
     * @return 对应的多个键值
     */

    public <T> Map<String, T> hmget( String key, Class<T> c) ;
    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */

    public void hdel(String key, Object... item) ;
    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */

    public boolean hHasKey(String key, String item) ;

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */

    public double hincr(String key, String item, double by) ;
    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */

    public double hdecr(String key, String item, double by) ;

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */

    public Set<Object> sGet(String key) ;
    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet( String key, Object... values) ;

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */

    public long sSetAndTime(String key, long time, Object... values) ;
    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */

    public boolean sHasKey(String key, Object value);

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) ;

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */

    public long sGetSetSize( String key) ;

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */


    @SuppressWarnings("unchecked")
    public <T> List<T> lGet( String key, long start, long end, final Class<T> c) ;

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */

    @SuppressWarnings("unchecked")
    public <T> T lGetIndex( String key, long index, final Class<T> c) ;
    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */

    public long lGetListSize(String key) ;

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */

    public boolean lSet(String key, Object value) ;

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */

    public boolean lSet(String key, Object value, long time) ;

    /**
     * 将list放入缓存
     *
     * @param key  键
     * @param list
     * @param <T>
     * @return
     */

    public <T> boolean lSetList( String key, final List<T> list) ;

    /**
     * 将list放入缓存
     *
     * @param key  键
     * @param list 值
     * @param time 时间(秒)
     * @return
     */

    public <T> boolean lSetList(String key, final List<T> list, long time);
    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value);

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。 count < 0 :
     *              从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。 count = 0 : 移除表中所有与
     *              VALUE 相等的值。
     * @param value 值
     * @return 移除的个数
     */

    public long lRemove( String key, long count, Object value) ;


    public long lpush(final String key,Object value);

    public long rpush(final String key,Object value);

    public String lpop(final String key);


    // ===============================有序集合=================================

    /**
     * Redis 有序集合和集合一样也是string类型元素的集合,且不允许重复的成员。
     * 不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。
     * 有序集合的成员是唯一的,但分数(score)却可以重复。
     */
    /**
     * 向集合中增加一条记录,如果这个值已存在，这个值对应的score将被置为新的score
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public boolean zSet( String key, Object value, double score) ;

    /**
     * 如果member是有序集key的成员，返回value的排名。如果value不是有序集key的成员，返回nil。 排名以0为底 存在返回 >-1
     * 不存在返回-1
     *
     * @param key
     * @param value
     * @return long
     */

    public long zRank(String key, Object value) ;

    /**
     * 获取指定成员的score值
     *
     * @param key
     * @param value
     * @return
     */


    public double zScore(String key, Object value) ;

    /**
     * 从有序集合中移除一个或者多个元素
     *
     * @param key
     * @param values
     * @return
     */
    public long zRemove(String key, Object... values);
    /**
     * 删除给score区间的元素
     *
     * @param key
     * @param min score下限(包含)
     * @param max score上限(包含)
     * @return 删除的数量
     */

    public long zRemoveRangeByScore( String key, double min, double max) ;
    /**
     * 获取有序集合的成员数
     *
     * @param key
     * @return
     */
    public long zCard(String key) ;
}
