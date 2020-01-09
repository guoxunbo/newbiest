package com.newbiest.common.stdf.input;

import com.newbiest.base.exception.ClientException;

import java.io.InputStream;

/**
 * Created by guoxunbo on 2019-11-28 18:37
 */
public interface DataFileReader {

    void addRecordUser(RecordUser recordUser);

    void readFile(InputStream inputStream, DataFile dataFile) throws Exception;

    void abortReading();
}
