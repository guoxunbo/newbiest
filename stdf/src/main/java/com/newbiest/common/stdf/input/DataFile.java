package com.newbiest.common.stdf.input;

import com.newbiest.base.utils.StringUtils;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

/**
 * Created by guoxunbo on 2019-11-28 19:00
 */
@Data
public class DataFile implements Comparable<DataFile>, Serializable {

    private File sourceFile;
    private String sourceFilePath;
    private String description;

    public DataFile(File sourceFile) {
        this.sourceFile = sourceFile;
        this.sourceFilePath = sourceFile.getAbsolutePath();
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
        if(StringUtils.isNullOrEmpty(sourceFilePath) && sourceFile != null) {
            sourceFilePath = sourceFile.getAbsolutePath();
        }
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
        if(sourceFile == null) {
            sourceFile = new File(sourceFilePath);
        }
    }

    public String getName() {
        return this.sourceFile == null ? StringUtils.EMPTY : this.sourceFile.getName();
    }

    @Override
    public int compareTo(DataFile otherDataFile) {
        if (otherDataFile == null) {
            return 0;
        }
        int result = this.sourceFile.compareTo(otherDataFile.sourceFile);
        if (result == 0) {
            result = this.sourceFilePath.compareTo(otherDataFile.sourceFilePath);
        }
        return result;
    }


}
