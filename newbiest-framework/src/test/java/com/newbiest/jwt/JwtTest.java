package com.newbiest.jwt;

import com.newbiest.main.FrameworkApplication;
import com.newbiest.main.JwtSigner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by guoxunbo on 2018/5/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
public class JwtTest {

    @Autowired
    JwtSigner jwtSigner;

    @Test
    public void validationTest() {
        try {
            String token = jwtSigner.sign("aaa");
            String str = jwtSigner.validate(token);
            Assert.assertEquals("aaa", str);
            Thread.sleep(1000 * 61 * 2);
            str = jwtSigner.validate(token);
            Assert.assertEquals("aaa", str);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
