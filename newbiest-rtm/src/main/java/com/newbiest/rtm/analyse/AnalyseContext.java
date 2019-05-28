package com.newbiest.rtm.analyse;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * 需要解析的context
 * Created by guoxunbo on 2019/5/27.
 */
@Data
public class AnalyseContext implements Serializable {

    /**
     * 需要解析的输入流
     */
    private InputStream inputStream;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 根据分析的context返回一个具体的解析类
     *  当前就一个故此处就返回固定的一个
     * @return
     */
    public IAnalyse match() {
        return new DynaxAnalyse();
    }
}
