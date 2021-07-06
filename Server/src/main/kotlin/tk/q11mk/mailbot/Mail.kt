package tk.q11mk.mailbot

import org.intellij.lang.annotations.Language
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object Mail {
    @Throws(MessagingException::class)
    fun sendEmail(sender: String?, receiver: String, password: String?, firstName: String, code: Int) {
        val host = "smtp.gmail.com"
        val properties = System.getProperties()
        properties["mail.smtp.host"] = host
        properties["mail.smtp.port"] = "465"
        properties["mail.smtp.ssl.enable"] = "true"
        properties["mail.smtp.auth"] = "true"
        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(sender, password) //agvbenttxknqgrji
            }
        })

        //session.setDebug(true);
        @Language("html") val htmlMessage =
            "<span style=\"text-align: center; background-color: white; width: 20%; font-size: large;\">" +
                    "<h1>Hallo " + firstName + ",</h1>" +
                    "<div style=\"background-color: #118BE6; margin: auto; margin-top: 50px; margin-bottom: 50px; border-radius: 10px; padding: 20px; width: 15%; color: white;\"><p>Ihr Code lautet :</p><p style=\"font-weight: bold; font-size: xl-large;\">" + code + "</p></div>" +
                    "<p>Mit freundlichen Grüßen,<br/>Ihr Maristenplaner Team.</p>" +
                    "</span>"

        //try {
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(sender))
        println("sent $receiver")
        message.addRecipient(Message.RecipientType.TO, InternetAddress(receiver))
        message.subject = "Bestätigungscode"
        message.setContent(htmlMessage, "text/html; charset=utf-8")
        Transport.send(message)
        // catch (MessagingException mex) {
        //   mex.printStackTrace();
        //
    }
}