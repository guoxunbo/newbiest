import com.newbiest.base.model.NBBase;
import com.newbiest.base.model.NBMessage;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.ui.model.NBSystemReferenceName;
import com.newbiest.base.ui.model.NBTable;
import com.newbiest.base.ui.service.UIService;
import com.newbiest.base.utils.CollectionUtils;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.service.GeneratorService;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import com.newbiest.main.NewbiestApplication;
import com.newbiest.security.model.NBOrg;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by guoxunbo on 2019/1/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NewbiestApplication.class)
@ActiveProfiles("dev")
public class Test {

    protected SessionContext sessionContext;

    @Autowired
    BaseService baseService;

    @Autowired
    GeneratorService generatorService;

    @Before
    public void init() {
        sessionContext = new SessionContext();
        sessionContext.setOrgRrn(1L);
    }

    @org.junit.Test
    public void generatorId() {

        GeneratorContext generatorContext = new GeneratorContext();
        generatorContext.setRuleName("CreateMLot");
        String id = generatorService.generatorId(sessionContext.getOrgRrn(), generatorContext);
        System.out.println(id);
    }
}
