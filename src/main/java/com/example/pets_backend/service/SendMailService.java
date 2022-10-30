package com.example.pets_backend.service;

import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Map;

import static com.example.pets_backend.ConstantValues.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendMailService {

    private final JavaMailSenderImpl mailSender;
    private final FreeMarkerConfigurer freemarkerConfigurer;

    public void sendEmail(String to, Map<String, String> model, String template, String rawIcs) throws Exception {
        log.info("Sending email to '{}'", to);
        Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate(template);
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, model);
        sendHtmlEmail(to, htmlBody, rawIcs);
    }

    public void sendVerifyEmail(String to, String text) throws MessagingException {
        log.info("Sending email to '{}'", to);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(TEAM_EMAIL);
        helper.setTo(to);
        helper.setSubject("PetPocket Email Verification");
        helper.setText(text, false);
        mailSender.send(message);
        log.info("Email has been sent to '{}'", to);
    }

    private void sendHtmlEmail(String to, String htmlBody, String rawIcs) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(TEAM_EMAIL);
        helper.setTo(to);
        helper.setSubject("Pet Pocket Reminder");
        // check whether the email has an ics attachment
        if (rawIcs.isEmpty()) {
            helper.setText(htmlBody, true);
        } else {
            MimeBodyPart iCalPart = new MimeBodyPart();
            DataSource iCalData = new ByteArrayDataSource(rawIcs, "text/calendar; charset=UTF-8");
            iCalPart.setDataHandler(new DataHandler(iCalData));
            iCalPart.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=PUBLISH");
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html");
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(iCalPart);
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
        }
        mailSender.send(message);
        log.info("Email has been sent to '{}'", to);
    }

}
