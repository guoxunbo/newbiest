package com.newbiest.base.redis;

import com.newbiest.main.FrameworkApplication;
import com.newbiest.security.model.NBUser;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by guoxunbo on 2018/6/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void put2() throws Exception {
        redisService.put("admin", "admin");
    }

    @Test
    public void put() throws Exception {
        NBUser nbUser = new NBUser();
        nbUser.setUsername("admin");
        redisService.put(nbUser.getUsername(), nbUser);
    }

    @Test
    public void get() throws Exception {
        put();
        NBUser redisUser = (NBUser) redisService.get("admin");
        assert redisUser != null;
        Assert.assertEquals("admin", redisUser.getUsername());
    }

    @Test
    public void contains() throws Exception {
        put();
        assert redisService.contains("admin");
    }

    @Test
    public void delete() throws Exception {
        put();
        redisService.delete("admin");
        assert !redisService.contains("admin");
    }

    @Test
    public void deleteAll() throws Exception {
        put();
        redisService.delete(Lists.newArrayList("admin"));
        assert !redisService.contains("admin");
    }

}