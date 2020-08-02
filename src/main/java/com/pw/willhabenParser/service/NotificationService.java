package com.pw.willhabenParser.service;

import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Component
public class NotificationService {

    public void sendMail(String address, String subject, String body) {
        String smtpHostServer = "smtp.example.com";
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHostServer);
        Session session = Session.getInstance(props, null);
        sendEmail(session, address, subject, body);
    }


    private void sendEmail(Session session, String address, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress("no_reply@willhaben-parser.herokuapp.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse("no_reply@willhaben-parser.herokuapp.com", false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
