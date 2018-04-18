package com.newbiest.base.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.newbiest.main.FrameworkApplication;
import com.newbiest.main.MailService;
import com.newbiest.security.model.NBUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * 邮件测试
 * Created by guoxunbo on 2018/1/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
public class MailUtilsTest {

    @Autowired
    private MailService mailService;

    @Test
    public void sendSimpleMessage() throws Exception{
        List<String> to = Lists.newArrayList("11603652@qq.com", "aguo@glorysoft.com");
        mailService.sendSimpleMessage(to, "test2", "test");
    }

    @Test
    public void sendTemplateMessage() throws Exception{
        List<String> to = Lists.newArrayList("11603652@qq.com", "aguo@glorysoft.com");

        Map<String, Object> map = Maps.newHashMap();
        NBUser nbUser = new NBUser();
        nbUser.setUsername("test");
        map.put("user", nbUser);
        mailService.sendTemplateMessage(to, "test2", MailService.CREATE_USER_TEMPLATE, map);
    }
}
