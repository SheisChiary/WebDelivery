package utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtility {
    
    
    private static final String MITTENTE = "114.bas@gmail.com";
  
    private static final String PASSWORD = "eccr lhkq yfdg zcvo"; 

    public static void inviaEmailRiepilogo(String destinatario, String oggetto, String messaggioHTML) {
        
        // Configurazioni per connettersi al server di Google (Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Autenticazione
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MITTENTE, PASSWORD);
            }
        });

        try {
            // Creazione dell'email
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(oggetto);
            
            
            message.setContent(messaggioHTML, "text/html; charset=utf-8");

            // SPEDISCI
            Transport.send(message);
            System.out.println("Email inviata con successo a: " + destinatario);

        } catch (MessagingException e) {
            System.out.println("Errore nell'invio dell'email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}