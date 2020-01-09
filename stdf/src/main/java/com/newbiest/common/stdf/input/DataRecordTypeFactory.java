package com.newbiest.common.stdf.input;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.*;

/**
 * Created by guoxunbo on 2020-01-08 17:32
 */
public class DataRecordTypeFactory implements Serializable {

    private static Map<String, DataRecordType> dataRecordTypes = Maps.newConcurrentMap();

    public static void putRecordType(String typeName, DataRecordType recordType) {
        dataRecordTypes.put(typeName, recordType);
    }

    public DataRecordType getType(String typeName) {
        if (dataRecordTypes.containsKey(typeName)) {
            return dataRecordTypes.get(typeName);
        }
        return null;
    }

    public DataRecordType getType(DataRecord instance) {
        return getType(instance.getRecordType());
    }

    public Collection<DataRecordType> getTypes() {
        return this.dataRecordTypes.values();
    }

}
