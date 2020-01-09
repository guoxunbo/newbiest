package com.newbiest.common.stdf.read;

import com.newbiest.common.stdf.input.DataRecord;

import java.nio.ByteBuffer;

/**
 * Created by guoxunbo on 2020-01-08 14:55
 */
public interface StdfUser {

    void finish();

    DataRecord processRecord(int length, short type, short subType, ByteBuffer buffer, long fileOffset);

    boolean isFinished();

}
