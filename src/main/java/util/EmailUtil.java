package util;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    // Indirizzo e porta standard per FakeSMTP in locale
    private static final String SMTP_HOST = "localhost";
    private static final String SMTP_PORT = "25"; 

    public static void inviaEmailConfermaOrdine(String destinatario, String nomeCliente, Long idOrdine, double totale) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ordini@webdelivery.it"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Riepilogo Ordine #" + idOrdine + " - WebDelivery");

            String testoHTML = "<h2>Grazie per il tuo ordine, " + nomeCliente + "!</h2>"
                    + "<p>Il tuo ordine <strong>#" + idOrdine + "</strong> è stato inserito con successo.</p>"
                    + "<p>Totale da pagare: <strong>€ " + String.format("%.2f", totale) + "</strong></p>"
                    + "<p>Riceverai un'ulteriore email quando l'ordine sarà in consegna.</p>";

            message.setContent(testoHTML, "text/html; charset=utf-8");
            Transport.send(message);
            
        } catch (Exception e) {
            System.err.println("Errore nell'invio della email di conferma (FakeSMTP acceso?): " + e.getMessage());
        }
    }

    public static void inviaEmailInConsegna(String destinatario, String nomeCliente, Long idOrdine) {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("ordini@webdelivery.it"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("L'ordine #" + idOrdine + " è IN CONSEGNA!");

            String testoHTML = "<h2>Ci siamo, " + nomeCliente + "!</h2>"
                    + "<p>Il rider è appena partito. Il tuo ordine <strong>#" + idOrdine + "</strong> è ufficialmente <strong>in consegna</strong>.</p>"
                    + "<p>Preparati ad aprire la porta!</p>";

            message.setContent(testoHTML, "text/html; charset=utf-8");
            Transport.send(message);
            
        } catch (Exception e) {
            System.err.println("Errore nell'invio della email in consegna: " + e.getMessage());
        }
    }
}