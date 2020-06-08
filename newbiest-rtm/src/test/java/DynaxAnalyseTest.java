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


            String path = this.getClass().getResource("/DWX19G64-37HC_15Dec2019_1448.txt").getPath();
            System.out.println(path);
            File file = new File(path);

            AnalyseContext context = new AnalyseContext();
            context.setFileName(file.getName());
            context.setInputStream(new FileInputStream(file));
            List<AnalyseResult> analyseResultList = analyse.analyse(context);
            assert analyseResultList.size() == 148;
            for (AnalyseResult result : analyseResultList) {
                System.out.println(result);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
