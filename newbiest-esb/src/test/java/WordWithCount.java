import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by guoxunbo on 2018/5/15.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordWithCount {
    public String word;
    public long count;
}
