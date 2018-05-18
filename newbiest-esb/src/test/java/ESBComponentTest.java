
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.Collection;

/**
 * Created by guoxunbo on 2018/5/15.
 */
public class ESBComponentTest {

    public static final String ARG_PORT = "port";

    public static void main(String[] args) {
        int port = 0;
        try {
            ParameterTool parameterTool = ParameterTool.fromArgs(args);
            port = parameterTool.getInt(ARG_PORT);
        } catch (Exception e) {
            System.err.println("There is no args named port. Please set port --port");
            return;
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> text = env.socketTextStream("localhost", port, "\n");

        DataStream<WordWithCount> windowCounts = text
                .flatMap(new FlatMapFunction<String, WordWithCount>() {
                    @Override
                    public void flatMap(String value, Collector<WordWithCount> out) {
                        for (String word : value.split("\\s")) {
                            out.collect(new WordWithCount(word, 1L));
                        }
                    }
                }).keyBy("word")
                .timeWindow(Time.seconds(5), Time.seconds(1))
                .reduce(new ReduceFunction<WordWithCount>() {
                    @Override
                    public WordWithCount reduce(WordWithCount a, WordWithCount b) {
                        return new WordWithCount(a.word, a.count + b.count);
                    }
                });
        windowCounts.print().setParallelism(1);

        try {
            env.execute("Socket Window WordCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


