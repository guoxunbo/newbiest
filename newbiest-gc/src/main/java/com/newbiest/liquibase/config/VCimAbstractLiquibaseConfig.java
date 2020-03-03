package com.newbiest.liquibase.config;

import com.newbiest.base.utils.StringUtils;
import com.newbiest.security.model.NBOrg;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.Data;

/**
 * Created by guoxunbo on 2020-03-03 16:51
 */
@Data
public class VCimAbstractLiquibaseConfig extends AbstractLiquibaseSerializable {

    public static final String SERIALIZE_OBJECT_NAME = "abstract";

    protected Long objectRrn;

    protected Long orgRrn = NBOrg.GLOBAL_ORG_RRN;

    protected String activeFlag = StringUtils.YES;

    @Override
    public String getSerializedObjectName() {
        return SERIALIZE_OBJECT_NAME;
    }

    @Override
    public String getSerializedObjectNamespace() {
        return SERIALIZE_OBJECT_NAME;
    }
}
