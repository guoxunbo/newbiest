package com.newbiest.common.stdf.read;

import com.newbiest.common.stdf.input.DataRecord;
import lombok.Data;

import java.nio.ByteBuffer;

/**
 * Created by guoxunbo on 2020-01-08 14:56
 */
@Data
public class StdfSimpleUser implements StdfUser {
    @Override
    public void finish() {

    }

    @Override
    public DataRecord processRecord(int i, short i1, short i2, ByteBuffer byteBuffer, long l) {
        return null;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
