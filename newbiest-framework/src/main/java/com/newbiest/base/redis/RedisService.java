package com.newbiest.base.redis;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.main.NewbiestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Redis 基本操作类 当前不涉及到集群
 * Created by guoxunbo on 2017/10/30.
 */
@Service
public class RedisService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    public void put(Object key, Object value) throws ClientException{
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            operations.set(key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public Object get(Object key) throws ClientException{
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            return operations.get(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public boolean contains(Object key) throws ClientException{
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    public void delele(List<Object> keys) throws ClientException{
        try {
            for (Object key : keys) {
                delele(key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }

    }

    public void delele(Object key) throws ClientException{
        try {
            if (contains(key)) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }

    }

}
