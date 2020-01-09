package com.newbiest.common.stdf.input;

import com.google.common.collect.Maps;
import com.newbiest.base.exception.ExceptionManager;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by guoxunbo on 2020-01-08 13:41
 */
@Slf4j
public class DataFileReaderFactory implements Serializable {

    private static Map<String, Class<? extends DataFileReader>> readers = Maps.newConcurrentMap();


    public static void registerReader(String type, Class<? extends DataFileReader> readerClass) {
        readers.put(type, readerClass);
    }

    public static DataFileReader getReader(String type){
        try {
            if (!readers.containsKey(type)) {
                return null;
            }
            Class<? extends DataFileReader> readerClass = readers.get(type);
            return readerClass.newInstance();
        } catch (Exception e){
            throw ExceptionManager.handleException(e, log);
        }
    }
}
