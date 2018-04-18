package com.newbiest.base.utils;

import com.newbiest.security.model.NBUser;
import com.newbiest.security.model.NBUserHis;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by guoxunbo on 2018/1/23.
 */
public class PropertyUtilsTest {
    @Test
    public void copyProperties() throws Exception {
        Date date = new Date();

        NBUser nbUser = new NBUser();
        nbUser.setUsername("admin");
        nbUser.setPwdLife(30L);
        nbUser.setPwdChanged(date);

        NBUserHis his = new NBUserHis();
        PropertyUtils.copyProperties(nbUser, his);
        assert his.getUsername().equals("admin");
        assert his.getPwdLife().equals(30L);
        assert his.getPwdChanged().equals(date);
    }

    @Test
    public void setProperty() throws Exception {
        NBUser nbUser = new NBUser();
        PropertyUtils.setProperty(nbUser, "activeFlag", false);
        PropertyUtils.setProperty(nbUser, "username", "aaa");
        PropertyUtils.setProperty(nbUser, "description", "aaa");

        assert nbUser.getActiveFlag() == false;
        assert nbUser.getUsername().equals("aaa");
        assert nbUser.getDescription().equals("aaa");

    }

    @Test
    public void getProperty() throws Exception {
        NBUser nbUser = new NBUser();
        nbUser.setUsername("admin");
        assert "admin".equals(PropertyUtils.getProperty(nbUser, "username"));
    }

}