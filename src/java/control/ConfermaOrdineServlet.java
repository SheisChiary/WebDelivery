package control;

import model.DBConnect;
import model.ElementoCarrello;
import utils.EmailUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("emailUtente") == null || session.getAttribute("idUtente") == null) {
                out.print("{\"successo\": false, \"messaggio\": \"Sessione scaduta, per favore fai di nuovo il login.\"}");
                return;
            }

            List<ElementoCarrello> carrello = (List<ElementoCarrello>) session.getAttribute("carrello");
            String email = (String) session.getAttribute("emailUtente");
            String nome = (String) session.getAttribute("nomeUtente");
            int idCliente = (int) session.getAttribute("idUtente"); 
            
            if (carrello == null || carrello.isEmpty()) {
                out.print("{\"successo\": false, \"messaggio\": \"Il carrello è vuoto!\"}");
                return;
            }

            String tipoOrario = request.getParameter("tipoOrario");
            String orarioScelto = request.getParameter("orario");
            int tempoStimatoCalcolato = 40; 
            double totaleOrdine = 2.50; // Costo base/spedizione

            for (ElementoCarrello el : carrello) {
                totaleOrdine += (el.getProdotto().getPrezzoBase() + el.getPrezzoExtra()) * el.getQuantita();
            }

            int numeroOrdine = 0;

            try (Connection conn = DBConnect.getConnection()) {
                conn.setAutoCommit(false); 
                
                String espressioneOrarioSQL = "DATE_ADD(NOW(), INTERVAL ? MINUTE)";
                boolean isProgrammato = "prog".equals(tipoOrario) && orarioScelto != null && !orarioScelto.trim().isEmpty();
                if (isProgrammato) { espressioneOrarioSQL = "CONCAT(CURDATE(), ' ', ?, ':00')"; }

                String sqlOrdine = "INSERT INTO Ordine (id_cliente, orario_consegna_richiesto, stato_attuale, prezzo_totale, tempo_stimato_consegna) VALUES (?, " + espressioneOrarioSQL + ", 'inserito', ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente); 
                    if (isProgrammato) { ps.setString(2, orarioScelto); } else { ps.setInt(2, tempoStimatoCalcolato); }
                    ps.setDouble(3, totaleOrdine); 
                    ps.setInt(4, tempoStimatoCalcolato); 
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) { numeroOrdine = rs.getInt(1); } }
                }
                
                if (numeroOrdine > 0) {
                    String sqlDettaglio = "INSERT INTO Dettaglio_Ordine (id_ordine, id_prodotto, quantita, prezzo_unitario) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement psDett = conn.prepareStatement(sqlDettaglio)) {
                        for (ElementoCarrello el : carrello) {
                            psDett.setInt(1, numeroOrdine);
                            psDett.setInt(2, el.getProdotto().getIdProdotto()); 
                            psDett.setInt(3, el.getQuantita());
                            psDett.setDouble(4, el.getProdotto().getPrezzoBase() + el.getPrezzoExtra());
                            psDett.executeUpdate();
                        }
                    }
                    String sqlStorico = "INSERT INTO Storico_Stati_Ordine (id_ordine, id_personale, stato) VALUES (?, ?, 'inserito')";
                    try (PreparedStatement psStorico = conn.prepareStatement(sqlStorico)) {
                        psStorico.setInt(1, numeroOrdine);
                        psStorico.setInt(2, idCliente); 
                        psStorico.executeUpdate();
                    }
                    conn.commit(); 
                } else {
                    conn.rollback();
                    out.print("{\"successo\": false, \"messaggio\": \"Errore interno durante la creazione dell'ordine.\"}");
                    return;
                }
            } catch (Exception dbEx) {
                dbEx.printStackTrace();
                out.print("{\"successo\": false, \"messaggio\": \"Errore fatale del database.\"}");
                return;
            }

            
            StringBuilder html = new StringBuilder();
            html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #fff; padding: 30px; border-radius: 12px; border: 1px solid #e5e7eb;'>");
            html.append("<h2 style='color: #116C4A; text-align: center; margin-top: 0;'>Riepilogo Ordine #" + numeroOrdine + "</h2>");
            html.append("<p style='color: #555; text-align: center; font-size: 16px;'>Grazie per aver scelto WebDelivery, <strong>").append(nome).append("</strong>! 🎉<br>Il tuo ordine è stato ricevuto ed è ora in preparazione.</p>");
            
            html.append("<div style='background: #f9fafb; padding: 20px; border-radius: 8px; margin-top: 25px;'>");
            html.append("<h3 style='margin-top: 0; color: #333; border-bottom: 2px solid #e5e7eb; padding-bottom: 10px;'>Dettaglio Prodotti:</h3>");
            html.append("<table style='width: 100%; border-collapse: collapse;'>");
            
            for (ElementoCarrello el : carrello) {
                double subTot = (el.getProdotto().getPrezzoBase() + el.getPrezzoExtra()) * el.getQuantita();
                html.append("<tr>");
                html.append("<td style='padding: 12px 0; border-bottom: 1px solid #e5e7eb; color: #333;'><strong>").append(el.getQuantita()).append("x</strong> ").append(el.getProdotto().getNome()).append("</td>");
                html.append("<td style='padding: 12px 0; border-bottom: 1px solid #e5e7eb; text-align: right; color: #333;'>€").append(String.format("%.2f", subTot)).append("</td>");
                html.append("</tr>");
            }
            
            html.append("<tr><td style='padding: 12px 0; color: #777;'>Spedizione / Servizio</td><td style='padding: 12px 0; text-align: right; color: #777;'>€2.50</td></tr>");
            html.append("</table>");
            html.append("<h2 style='text-align: right; color: #116C4A; margin-top: 20px; border-top: 2px solid #e5e7eb; padding-top: 15px;'>Totale: €").append(String.format("%.2f", totaleOrdine)).append("</h2>");
            html.append("</div>");
            
            html.append("<p style='text-align: center; color: #777; font-size: 13px; margin-top: 30px;'>Ti invieremo un'altra email non appena il tuo ordine sarà in arrivo!</p>");
            html.append("</div>");

            try { EmailUtility.inviaEmailRiepilogo(email, " Il tuo ordine #" + numeroOrdine + " è confermato!", html.toString()); } 
            catch (Throwable t) { System.out.println("Email non inviata"); }
            
            session.removeAttribute("carrello");
            out.print("{\"successo\": true, \"messaggio\": \"Ordine confermato con successo!\"}");

        } catch (Throwable t) {
            t.printStackTrace();
            out.print("{\"successo\": false, \"messaggio\": \"Errore imprevisto del server.\"}");
        }
    }
}