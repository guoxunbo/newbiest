package com.newbiest.common.stdf.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Created by guoxunbo on 2020-01-08 17:40
 */
@Data
@AllArgsConstructor
public class DataRecordType implements Comparable<DataRecordType> {

    private String typeName;

    private String description;

    private Set<String> attributes;

    @Override
    public int compareTo(DataRecordType other) {
        return other == null ? -1 : this.typeName.compareTo(other.typeName);
    }

}
