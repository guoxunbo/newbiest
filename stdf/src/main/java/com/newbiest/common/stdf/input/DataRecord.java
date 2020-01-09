package com.newbiest.common.stdf.input;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by guoxunbo on 2019-11-28 18:57
 */
public interface DataRecord extends Serializable, DefinedRecordType {
    
    int getRecordNumber();

    void setRecordNumber(int recordNumber);

    String getRecordType();

    String getDescription();

    DataFile getSourceFile();

    void setSourceFile(DataFile sourceFile);

//    void writeRecord(SaveInfo paramSaveInfo, ByteBuffer paramByteBuffer);

    void readRecord(ByteBuffer byteBuffer);
}
