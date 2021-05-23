package com.xlg.component.utils;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-05-23
 * 任务开始发送邮件
 */
@Service
public class MailSendUtils {

    private final Logger logger = LoggerFactory.getLogger(MailSendUtils.class);

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送文本邮件
     * @param to
     * @param subject
     * @param content
     */
    public void sendSimpleMail(String to, String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(from);
        mailSender.send(message);
    }

    /**
     * 发送带html的邮件
     */
    public void sendHtmlMail(String to,String subject,String content) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);
        mailSender.send(mimeMessage);
    }

    /**
     * 发送带副本
     */
    public void sendAttachmentMail(String to,String subject,String content,String filepath) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content,true);

        //文件流:获取本地文件
        FileSystemResource file = new FileSystemResource(new File(filepath));
        String filename = file.getFilename();
        //可以发送多个
        helper.addAttachment(filename,file);
        // helper.addAttachment(filename+"_test",file);

        //进行发送
        mailSender.send(message);
    }

    /**
     * 发送带图片
     */
    public void sendImageMail(String to,String subject,String content,String rscPath,String rscId){
        logger.info("发送静态邮件开始: {},{},{},{},{}",to,subject,content,rscPath,rscId);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try{
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);

            FileSystemResource file = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId,file);
            mailSender.send(message);
            logger.info("发送静态图片邮件成功!");
        }catch (Exception e){
            logger.error("发送静态邮件失败!",e);
        }
    }

}
