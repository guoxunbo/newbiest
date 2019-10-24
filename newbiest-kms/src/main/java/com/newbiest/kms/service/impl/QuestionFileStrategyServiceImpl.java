package com.newbiest.kms.service.impl;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.model.NBBase;
import com.newbiest.base.service.BaseService;
import com.newbiest.base.service.impl.DefaultFileStrategyServiceImpl;
import com.newbiest.base.utils.PropertyUtils;
import com.newbiest.base.utils.StringUtils;
import com.newbiest.base.utils.ThreadLocalContext;
import com.newbiest.kms.KmsConfiguration;
import com.newbiest.kms.KmsException;
import com.newbiest.kms.model.Question;
import com.newbiest.kms.model.QuestionLine;
import com.newbiest.kms.service.KmsService;
import com.newbiest.security.model.NBUser;
import com.newbiest.security.service.SecurityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Created by guoxunbo on 2019-08-08 10:03
 */
@Component
@Slf4j
public class QuestionFileStrategyServiceImpl extends DefaultFileStrategyServiceImpl {

    @Autowired
    KmsConfiguration kmsConfiguration;

    @Autowired
    BaseService baseService;

    @Autowired
    SecurityService securityService;

    @Override
    public NBBase uploadFile(NBBase nbBase, String propertyName, String fileName, InputStream inputStream) throws ClientException {
        try {
            Question question = (Question) baseService.findEntity(nbBase, false);
            validationUploadAuthority(question);

            File file = new File(getFilePath(nbBase) + File.separator + fileName);
            Files.createParentDirs(file);
            Files.write(ByteStreams.toByteArray(inputStream), file);

            PropertyUtils.setProperty(nbBase, propertyName, fileName);
            nbBase = baseService.saveEntity(question);
            return nbBase;
        } catch (Exception e) {
            throw ExceptionManager.handleException(e, log);
        }
    }

    @Override
    public String getFilePath(NBBase nbBase) throws ClientException {
        return kmsConfiguration.getQuestionPath();
    }

    /**
     * 验证权限
     * 当没有上传附件的时候，任意人都可以上传附件。不局限于创建人和指定人
     * 当上传了附件之后，只有上传者可以再次上传附件。哪怕是创建人和指定人都不能再次上传。
     */
    private void validationUploadAuthority(Question question) {
        if (!StringUtils.isNullOrEmpty(question.getReserved7())) {
            if (!question.getReserved7().equals(ThreadLocalContext.getUsername())){
                throw new ClientException(KmsException.AUTH_IS_NOT_ALLOW);
            }
        } else {
            NBUser user = securityService.getUserByUsername(ThreadLocalContext.getUsername());
            question.setReserved7(ThreadLocalContext.getUsername());
            question.setReserved8(user.getDescription());
        }
    }

}
