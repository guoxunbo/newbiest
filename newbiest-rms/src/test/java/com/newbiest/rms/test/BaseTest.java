package com.newbiest.rms.test;

import com.newbiest.base.utils.SessionContext;
import com.newbiest.rms.main.RmsApplication;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by guoxunbo on 2018/7/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RmsApplication.class)
public class BaseTest {

    protected SessionContext sessionContext;

    @Before
    public void init() {
        sessionContext = new SessionContext();
        sessionContext.setOrgRrn(10L);
    }

}
