package com.brandpark.sharemusic.infra.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Profile("dev")
@Slf4j
@RequiredArgsConstructor
@Service
public class SendGridMailService implements MailService{

    private final SendGrid sendGrid;

    public void send(MailMessage message) {
        Email from = new Email("alsrhs0530@gmail.com");
        String subject = message.getSubject();
        Email to = new Email(message.getTo());
        Content content = new Content("text/html", message.getText());

        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("response-body : {}", response.getBody());
        } catch (IOException ex) {
            log.error("failed to send email", ex);
        }
    }
}
