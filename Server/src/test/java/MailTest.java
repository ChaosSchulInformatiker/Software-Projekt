import tk.q11mk.mailbot.Mail;

import javax.mail.MessagingException;

public class MailTest {
    public static void main(String[] args) {
        try {
            Mail.sendEmail("noreply.maristenplaner@gmail.com","leo.suessmeyer@maristenkolleg.de","agvbenttxknqgrji","Leo",12);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
