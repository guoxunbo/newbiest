import com.newbiest.rtm.analyse.AnalyseContext;
import com.newbiest.rtm.analyse.DynaxAnalyse;
import com.newbiest.rtm.analyse.IAnalyse;
import com.newbiest.rtm.model.AnalyseResult;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by guoxunbo on 2019/5/27.
 */
public class DynaxAnalyseTest {

    @Test
    public void test() {
        try {
            IAnalyse analyse = new DynaxAnalyse();
            File file = new File("/Users/apple/Desktop/D1H38140C1_D1923232_25Feb2019_1032_Site0.txt");

            AnalyseContext context = new AnalyseContext();
            context.setFileName(file.getName());
            context.setInputStream(new FileInputStream(file));
            List<AnalyseResult> analyseResultList = analyse.analyse(context);
            assert analyseResultList.size() == 64;
            for (AnalyseResult result : analyseResultList) {
                System.out.println(result);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
