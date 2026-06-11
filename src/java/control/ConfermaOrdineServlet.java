package control;

import model.ElementoCarrello;
import utils.EmailUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ConfermaOrdineServlet", urlPatterns = {"/ConfermaOrdineServlet"})
public class ConfermaOrdineServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("emailUtente") == null) {
            out.print("{\"successo\": false, \"messaggio\": \"Sessione scaduta\"}");
            return;
        }

        List<ElementoCarrello> carrello = (List<ElementoCarrello>) session.getAttribute("carrello");
        String email = (String) session.getAttribute("emailUtente");
        String nome = (String) session.getAttribute("nomeUtente");
        
        if (carrello == null || carrello.isEmpty()) {
            out.print("{\"successo\": false, \"messaggio\": \"Carrello vuoto\"}");
            return;
        }

        // Generiamo un numero ordine univoco basato sul tempo
        String numeroOrdine = "WD-" + System.currentTimeMillis();
        double totaleOrdine = 2.50; // Includiamo subito la consegna

        // Costruiamo l'HTML dell'email
        StringBuilder html = new StringBuilder();
        html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
        html.append("<h2 style='color: #116C4A;'>Grazie per il tuo ordine, ").append(nome).append("!</h2>");
        html.append("<p>Il tuo ordine <strong>#").append(numeroOrdine).append("</strong> è stato ricevuto con successo.</p>");
        html.append("<hr style='border: 0; border-top: 1px solid #eee;'>");
        html.append("<h3>Riepilogo Ordine:</h3>");
        html.append("<table style='width: 100%; border-collapse: collapse;'>");
        html.append("<tr style='background: #f4f4f4;'><th>Prodotto</th><th>Qtà</th><th>Prezzo</th></tr>");

        for (ElementoCarrello el : carrello) {
            double riga = (el.getProdotto().getPrezzoBase() + el.getPrezzoExtra()) * el.getQuantita();
            totaleOrdine += riga;
            html.append("<tr>")
                .append("<td style='padding: 8px;'>").append(el.getProdotto().getNome()).append("</td>")
                .append("<td style='padding: 8px; text-align: center;'>").append(el.getQuantita()).append("</td>")
                .append("<td style='padding: 8px; text-align: right;'>€").append(String.format("%.2f", riga)).append("</td>")
                .append("</tr>");
        }
        
        html.append("</table>");
        html.append("<p style='font-size: 18px; font-weight: bold; text-align: right;'>Totale: €").append(String.format("%.2f", totaleOrdine)).append("</p>");
        html.append("<p>La consegna è stimata in 30-40 minuti.</p>");
        html.append("<br><p style='color: #888;'>A presto,<br><strong>Il Team WebDelivery</strong></p>");
        html.append("</div>");

        try {
            EmailUtility.inviaEmailRiepilogo(email, "Conferma Ordine #" + numeroOrdine, html.toString());
            session.removeAttribute("carrello");
            out.print("{\"successo\": true, \"messaggio\": \"Ordine confermato! Controlla la tua email.\"}");
        } catch (Exception e) {
            out.print("{\"successo\": false, \"messaggio\": \"Errore invio email\"}");
        }
    }}