package com.newbiest.common.idgenerator.model;

import com.newbiest.base.model.NBBase;
import com.newbiest.base.ui.model.NBOwnerReferenceList;
import com.newbiest.base.ui.model.NBReferenceList;
import com.newbiest.base.utils.SessionContext;
import com.newbiest.common.idgenerator.utils.GeneratorContext;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * Created by guoxunbo on 2018/8/3.
 */
@Entity
@Table(name = "COM_GENERATOR_RULE_LINE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DATA_TYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
@Data
public class GeneratorRuleLine extends NBBase {

    protected static final String GENERATOR_ERROR_CODE = "_";
    protected static final String EXCLUDE_SPLIT_CODE = ";";

    /**
     * Fixed String
     */
    public static final String DATA_TYPE_FIXED_STRING = "F";

    /**
     * Date Time Value
     */
    public static final String DATA_TYPE_DATETIME = "D";

    /**
     * Variable Value
     */
    public static final String DATA_TYPE_VARIABLE = "V";

    /**
     * Sequence Value
     */
    public static final String DATA_TYPE_SEQUENCE = "S";

    @Column(name="RULE_RRN")
    protected Long ruleRrn;

    @Column(name="SEQ_NO")
    protected Long seqNo;

    @Column(name="LENGTH")
    protected Long length;

    @Column(name="DATA_TYPE", insertable=false, updatable=false)
    protected String dataType;

    /**
     * 用户栏位参考值中的参考名称
     */
    @Column(name = "REFERENCE_NAME")
    protected String referenceName;

    public String generator(GeneratorContext context) throws Exception {
        return GENERATOR_ERROR_CODE;
    }

    protected String getReferenceValue(String key, GeneratorContext context) {
        List<NBOwnerReferenceList> formatCodes = (List<NBOwnerReferenceList>) context.getUiService().getReferenceList(referenceName, NBReferenceList.CATEGORY_OWNER, SessionContext.buildSessionContext(orgRrn));
        Optional<NBOwnerReferenceList> optional =
                formatCodes.stream().filter(nbOwnerReferenceList -> nbOwnerReferenceList.getKey().equals(key)).findFirst();
        if (optional.isPresent()) {
            return optional.get().getValue();
        }
        return key;
    }

}
