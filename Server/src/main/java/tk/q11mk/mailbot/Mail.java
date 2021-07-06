package tk.q11mk.mailbot;

import org.intellij.lang.annotations.Language;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

    public static void sendEmail(String sender, String receiver, String password, String firstName, int code) throws MessagingException {

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password); //agvbenttxknqgrji
            }

        });

        //session.setDebug(true);
        @Language("html") String htmlMessage =
                "<span style=\"text-align: center; background-color: white; width: 20%; font-size: large;\">" +
                "<h1>Hallo "+firstName+",</h1>" +
                "<div style=\"background-color: #0D68AA; margin: auto; margin-top: 50px; margin-bottom: 50px; border-radius: 10px; padding: 20px; width: 15%; color: white;\"><p>Ihr Code lautet :</p><p style=\"font-weight: bold; font-size: xl-large;\">"+code+"</p></div>" +
                "<p>Mit freundlichen Grüßen,<br/>Ihr Maristenplaner Team.</p>" +
                "</span>";

        //try {
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(sender));
        System.out.println("sent "+receiver);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
        message.setSubject("Bestätigungscode");
        message.setContent(htmlMessage, "text/html; charset=utf-8");
        Transport.send(message);
        // catch (MessagingException mex) {
        //   mex.printStackTrace();
        //
    }
}
