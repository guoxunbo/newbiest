package com.newbiest.vanchip.service.impl;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.impl.DefaultFileStrategyServiceImpl;
import com.newbiest.vanchip.VanchipConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

@Slf4j
@Component
public class PackingListFileStrategyServiceImpl extends DefaultFileStrategyServiceImpl{

    @Autowired
    VanchipConfiguration vanchipConfiguration;

    public String getFilePath(NBBase nbBase) throws ClientException {

        return vanchipConfiguration.getPackingListFilePath();
    }

    @Override
    public NBBase uploadFile(NBBase nbBase, String propertyName, String fileName, InputStream inputStream) throws ClientException {
        try {
            File file = new File(getFilePath(null) + File.separator + fileName);
            Files.createParentDirs(file);

            Files.write(ByteStreams.toByteArray(inputStream), file);
            return nbBase;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }
}
