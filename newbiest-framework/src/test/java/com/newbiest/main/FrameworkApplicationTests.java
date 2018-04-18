package com.newbiest.main;

import com.google.common.collect.Lists;
import com.newbiest.base.redis.RedisService;
import com.newbiest.base.utils.EncryptionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.repository.RoleRepository;
import com.newbiest.security.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.beans.PropertyDescriptor;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
public class FrameworkApplicationTests {

	@Autowired
	private RedisService redisService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	public void test() {
	}

}
