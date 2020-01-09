package com.newbiest.common.stdf.datarecord;

import com.google.common.collect.Sets;
import com.newbiest.common.stdf.input.DataFile;
import com.newbiest.common.stdf.input.DataRecord;
import com.newbiest.common.stdf.input.DataRecordType;
import com.newbiest.common.stdf.input.DataRecordTypeFactory;
import com.newbiest.common.stdf.read.FieldReader;
import lombok.Data;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;

/**
 * File Attributes Record
 * 参考 stdf_v4_types.xml
 * Created by guoxunbo on 2020-01-08 17:10
 */
@Data
public class FAR implements DataRecord {

    public static final String RECORD_TYPE = "FAR";
    public static final String RECORD_TYPE_DESCRIPTION = "FileAttributesRecord";

    public static final String ATTRIBUTE_CPU_TYPE = "CPU_TYPE";
    public static final String ATTRIBUTE_STDF_VERSION = "STDF_VER";

    public static final short HEAD_TYPE = 0;
    public static final short HEAD_SUB_TYPE = 10;

    private short cpuType;
    private short stdfVersion;
    private transient int recordNumber;
    private transient DataFile sourceFile;

    static {
        addType();
    }

    public static void addType() {
        LinkedHashSet<String> attributeSet = Sets.newLinkedHashSet();
        attributeSet.add(ATTRIBUTE_CPU_TYPE);
        attributeSet.add(ATTRIBUTE_STDF_VERSION);
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
    public void setSourceFile(DataFile paramDataFile) {
        this.sourceFile = sourceFile;
    }

//    @Override
//    public void writeRecord(SaveInfo paramSaveInfo, ByteBuffer paramByteBuffer) {
//
//    }

    @Override
    public void readRecord(ByteBuffer buffer) {
        this.cpuType = FieldReader.readUnsignedByte(buffer);
        this.stdfVersion = FieldReader.readUnsignedByte(buffer);
    }
}
