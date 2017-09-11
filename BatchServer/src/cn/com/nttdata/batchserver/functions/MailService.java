package cn.com.nttdata.batchserver.functions;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import cn.com.nttdata.batchserver.errors.MailServiceError;

final class MailService {
    private static final String mailServerHost = "client.nttdatabj.com.cn";
    private static final String mailServerPort = "25";
    private static final String fromAddress = "peng.guo@nttdata.com";
    private static String toAddresses;
    private static final String userName = "guopeng02";
    private static final String password = "(Nttdata)";
    private static boolean validate = true;
    private static String subject;
    private static String content;

    static String getContent() {
        return content;
    }

    static void setContent(String content) {
        MailService.content = content;
    }

    private static Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", mailServerHost);
        p.put("mail.smtp.port", mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }

    static boolean isValidate() {
        return validate;
    }

    static String getToAddresses() {
        return toAddresses;
    }

    static void setToAddresses(String toAddresses) {
        MailService.toAddresses = toAddresses;
    }

    static String getSubject() {
        return subject;
    }

    static void setSubject(String subject) {
        MailService.subject = subject;
    }

    static String getMailServerHost() {
        return mailServerHost;
    }

    static String getMailServerPort() {
        return mailServerPort;
    }

    static String getFromAddress() {
        return fromAddress;
    }

    static String getUserName() {
        return userName;
    }

    static String getPassword() {
        return password;
    }

    static void setValidate(boolean validate) {
        MailService.validate = validate;
    }

    @Deprecated
    static void send(Logger logger) {
        Session session = Session.getInstance(getProperties(), new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            Address from = new InternetAddress(fromAddress);
            message.setFrom(from);
            Address to = new InternetAddress(toAddresses);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setText(content);
            Transport.send(message);
        } catch(MessagingException error) {
            logger.error("メッセージ発信中に失敗しました："+ error.getMessage());
            throw new MailServiceError(error.getMessage(), error);
        } catch(Error error) {
            logger.error("メッセージ発信中に失敗しました："+ error.getMessage());
            throw new MailServiceError(error.getMessage(), error);
        }
    }

    static void sendMessage(Logger logger) {
        Transport transport = null;
        Session session = Session.getInstance(getProperties(), new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(false);
        MimeMessage message = new MimeMessage(session);
        try {
            Address from = new InternetAddress(fromAddress);
            message.setFrom(from);
            String[] allTo = toAddresses.split(",");
            InternetAddress[] toAddress = new InternetAddress[allTo.length];
            for(int i = 0 ; i < allTo.length ; i++) {
                toAddress[i]=new InternetAddress(allTo[i]);
            }
            message.addRecipients(Message.RecipientType.BCC, toAddress);
            message.setSubject(subject);
            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setText(content);
            multipart.addBodyPart(contentPart);
            message.setContent(multipart);
            message.saveChanges();
            transport = session.getTransport("smtp");
            transport.connect(mailServerHost, userName, password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch(MessagingException error) {
            logger.error("メッセージ発信中に失敗しました："+ error.getMessage());
            throw new MailServiceError(error.getMessage(), error);
        } catch(Error error) {
            logger.error("メッセージ発信中に失敗しました："+ error.getMessage());
            throw new MailServiceError(error.getMessage(), error);
        } finally {
            if(transport != null) {
                try {
                    transport.close();
                } catch(MessagingException e) {}
            }
        }
    }
}
