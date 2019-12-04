package com.newbiest.common.stdf.input;

/**
 * Created by guoxunbo on 2019-11-28 18:52
 */
public interface RecordUser {

    void processRecord(DataRecord paramDataRecord, long paramLong);

    public abstract void finish();
}
