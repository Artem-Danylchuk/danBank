package Bank.backBank.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    private MailProperties mailProperties;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);


    @Autowired
    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }
    @Transactional
    public void sendPasswordResetEmail(String to, String templateName, Context context) throws MessagingException {
        logger.error("start send");

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setSubject("Password Reset");
            helper.setFrom("danbank.help@gmail.com"); //danbank.help@gmail.com

            helper.setTo(to);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        logger.error("start send done");

    }
    @Transactional
    public void sendEmailFromGuest(String name, String from,String subject,String body) throws MessagingException {
        logger.error("start send");

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setSubject(subject);
        helper.setFrom("danbank.help@gmail.com");
        helper.setTo("danbank.help@gmail.com");
        helper.setText("Name - "+ name + ". \nMail from - "+ from + "\n"+body);
        javaMailSender.send(message);

    }

}
