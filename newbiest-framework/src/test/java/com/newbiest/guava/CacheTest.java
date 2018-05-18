package com.newbiest.guava;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by guoxunbo on 2018/5/7.
 */
public class CacheTest {

    @Test
    public void cacheTest() {

        // 创建缓存容器
        LoadingCache<String,String> cache = CacheBuilder.newBuilder()
                //最大缓存数目
                .maximumSize(100)
                //当缓存在一定时间内没有被访问 1秒
                .expireAfterAccess(1, TimeUnit.SECONDS)
                //当缓存在一定时间内没有被读写1秒
                //  .expireAfterWrite(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        // 当key的值在value中不存在的时候调用此方法，
                        // 如果有很明确的loading业务，可以在此处进行方法调用，
                        // 更加方便的明了知道此处是如何加载缓存的
                        // 即如果有值则返回，没值则逻辑运算之后返回值
                        return key;
                    }
                });
        // 显示插入 覆盖原有值
        cache.put("j","java");
        cache.put("c","cpp");
        cache.put("s","scala");
        cache.put("g","go");

        try {
            Assert.assertEquals("java", cache.get("j"));
            // 沉睡2秒让缓存过期
            TimeUnit.SECONDS.sleep(2);
            // 上面方法当value不存在的时候直接返回key
            Assert.assertEquals("s", cache.get("s"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
