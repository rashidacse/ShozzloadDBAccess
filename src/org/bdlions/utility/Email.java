package org.bdlions.utility;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author nazmul 
 */
public class Email {
    public Email()
    {
    
    }
    
    public void sendEmail(String receiverEmail, String messageBody)
    {
        final String username = "zoom.system123@gmail.com";
        final String password = "z123system";

        Properties props = new Properties();
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        //this will be used to debug mail sending procedure.
        //props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                }
          });

        try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("zoom.system123@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(receiverEmail));
                message.setSubject("Transaction Code");
                message.setText(messageBody);

                Transport.send(message);

                System.out.println("Email is sent successfully.");

        } catch (MessagingException e) {
                throw new RuntimeException(e);
        }
    }
}
