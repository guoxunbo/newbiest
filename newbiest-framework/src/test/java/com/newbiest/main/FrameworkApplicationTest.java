package com.newbiest.main;

import com.newbiest.base.utils.SessionContext;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
public class FrameworkApplicationTest {

	protected SessionContext sessionContext;

	@Before
	public void init() {
		sessionContext = new SessionContext();
		sessionContext.setOrgRrn(10L);
	}

}
