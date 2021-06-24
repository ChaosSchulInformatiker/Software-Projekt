package tk.q11mk.mailbot;

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

        //try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject("Bestätigungscode");
            message.setText("Hallo "+firstName+",\n\nDein Code lautet: "+code+"\n\nMit freundlichen Grüßen,\n\nIhr Maristenplaner-Team");
            Transport.send(message);
        // catch (MessagingException mex) {
        //   mex.printStackTrace();
        //
    }
}
