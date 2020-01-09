package com.newbiest.common.stdf.datarecord;

import com.google.common.collect.Sets;
import com.newbiest.common.stdf.input.DataFile;
import com.newbiest.common.stdf.input.DataRecord;
import com.newbiest.common.stdf.input.DataRecordType;
import com.newbiest.common.stdf.input.DataRecordTypeFactory;
import com.newbiest.common.stdf.read.FieldReader;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;

/**
 * 参考 stdf_v4_types.xml
 * Created by guoxunbo on 2020-01-09 15:53
 */
public class ATR implements DataRecord {

    public static final String RECORD_TYPE = "ATR";
    public static final String RECORD_TYPE_DESCRIPTION = "AuditTrailRecord";

    public static final String ATTRIBUTE_MOD_TIM = "MOD_TIM";
    public static final String ATTRIBUTE_CMD_LINE = "CMD_LINE";

    public static final short HEAD_TYPE = 0;
    public static final short HEAD_SUB_TYPE = 20;

    private long modTime;
    private String cmdLine;
    private transient int recordNumber;
    private transient DataFile sourceFile;

    static {
        addType();
    }

    public static void addType() {
        LinkedHashSet<String> attributeSet = Sets.newLinkedHashSet();
        attributeSet.add(ATTRIBUTE_MOD_TIM);
        attributeSet.add(ATTRIBUTE_CMD_LINE);
        DataRecordTypeFactory.putRecordType(RECORD_TYPE, new DataRecordType(RECORD_TYPE, RECORD_TYPE_DESCRIPTION, attributeSet));
    }

    @Override
    public int getRecordNumber() {
        return this.recordNumber;
    }

    @Override
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }

    @Override
    public String getRecordType() {
        return RECORD_TYPE;
    }

    @Override
    public String getDescription() {
        return RECORD_TYPE_DESCRIPTION;
    }

    @Override
    public DataFile getSourceFile() {
        return sourceFile;
    }

    @Override
    public void setSourceFile(DataFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public void readRecord(ByteBuffer buffer) {
        this.modTime = FieldReader.readUnsignedInt(buffer);
        this.cmdLine = FieldReader.readVariableLengthString(buffer);
    }
}
