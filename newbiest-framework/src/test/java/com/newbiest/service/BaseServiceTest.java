package com.newbiest.service;

import com.newbiest.base.model.NBMessage;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.main.FrameworkApplicationTest;
import com.newbiest.security.model.NBOrg;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Created by guoxunbo on 2018/7/20.
 */
@ActiveProfiles("dev")
public class BaseServiceTest extends FrameworkApplicationTest {

    @Autowired
    BaseService baseService;

    @Test
    public void findAll() {
        List<NBMessage> messageList = (List<NBMessage>) baseService.findAll(NBMessage.class.getName(), NBOrg.GLOBAL_ORG_RRN);
        assert CollectionUtils.isNotEmpty(messageList);
    }

}
