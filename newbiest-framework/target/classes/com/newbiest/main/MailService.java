package com.newbiest.main;

import com.newbiest.base.annotation.MethodMonitor;
import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ExceptionManager;
import com.newbiest.base.utils.CollectionUtils;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件操作相关类 不参与任何事务，邮件服务发送需要时间，故所有方法异步执行
 * Created by guoxunbo on 2017/10/7.
 */
@Component
@EnableAsync
@Transactional(propagation = Propagation.NEVER)
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String CREATE_USER_TEMPLATE = "create_user";
    public static final String RESET_PASSWORD_TEMPLATE = "reset_password";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${spring.mail.from}")
    private String from;

    /**
     * 发送普通文本协议邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Async
    @MethodMonitor
    public void sendSimpleMessage(List<String> to, String subject, String content) throws ClientException{
        try {
            if (to == null || to.isEmpty()) {
                throw new ClientException("");
            }
            String[] mailTo = to.toArray(new String[to.size()]);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(mailTo);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            if (logger.isDebugEnabled()) {
                logger.debug("Send Mail OK! To [" + to + "] and content is [" + content + "]");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 发送带有附件的邮件 当前写法需要先把file上传到服务器上然后进行发送
     * @param to
     * @param subject
     * @param content
     * @param fileNames
     * @throws ClientException
     */
    @Async
    @MethodMonitor
    public void sendAttachmentMessage(List<String> to, String subject, String content, List<String> fileNames) throws ClientException{
        try {
            if (to == null || to.isEmpty()) {
                throw new ClientException("");
            }
            String[] mailTo = to.toArray(new String[to.size()]);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content);
            if (CollectionUtils.isNotEmpty(fileNames)) {
                for (String fileName : fileNames) {
                    FileSystemResource fileSystemResource = new FileSystemResource(new File(fileNames.get(0)));
                    // TODO 支持上传附件直接以流的形式进行发送
                    //InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                    mimeMessageHelper.addAttachment(fileName, fileSystemResource);

                }
            }
            mailSender.send(mimeMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Send Attachment Mail OK! To [" + to + "] and content is [" + content + "] " +
                        "and attachment is [" + fileNames.toString() + "]");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 不把资源当成附件，直接显示在页面上 当前只支持图片
     * @param to
     * @param subject
     * @param fileNames
     * @throws ClientException
     */
    @Async
    @MethodMonitor
    public void sendInlineImageMessage(List<String> to, String subject, List<String> fileNames) throws ClientException{
        try {
            if (to == null || to.isEmpty()) {
                throw new ClientException("");
            }
            if (fileNames == null || fileNames.isEmpty()) {
                return;
            }

            String[] mailTo = to.toArray(new String[to.size()]);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            StringBuffer textBuffer = new StringBuffer();
            textBuffer.append("<html><body>");
            for (String fileName : fileNames) {
                textBuffer.append("<img src=\"cid:" + fileName + "\">");
            }
            textBuffer.append("/body></html>");
            mimeMessageHelper.setText(textBuffer.toString(), true);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setSubject(subject);

            for (String fileName : fileNames) {
                FileSystemResource fileSystemResource = new FileSystemResource(new File(fileNames.get(0)));
                mimeMessageHelper.addInline(fileName, fileSystemResource);
            }
            mailSender.send(mimeMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Send Inline Mail OK! To [" + to + "] and content is [" + textBuffer.toString() + "] " +
                        "and InLineFile is [" + fileNames.toString() + "]");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

    /**
     * 根据模板进行发送邮件
     * @param to
     * @param subject
     * @param templateName 模板名称
     * @param parameterMap 模板参数
     * @throws ClientException
     */
    @MethodMonitor
    public void sendTemplateMessage(List<String> to, String subject, String templateName, Map<String, Object> parameterMap) throws ClientException{
        try {
            if (to == null || to.isEmpty()) {
                throw new ClientException("");
            }
            String[] mailTo = to.toArray(new String[to.size()]);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setSubject(subject);

            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            if (template == null) {
                throw new ClientException("");
            }
            // 此处的parameterMap主要是为template中的${}的对象进行赋值
            // <h3>你好， ${username}, 模板邮件!</h3> 如此处的username
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, parameterMap);
            mimeMessageHelper.setText(content, true);
            mailSender.send(mimeMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Send Mail OK! To [" + to + "] and content is [" + content + "] ");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw ExceptionManager.handleException(e);
        }
    }

}
