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
            
            // Controllo della sessione
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

            // Calcolo del totale
            double totaleOrdine = 2.50; // Spedizione inclusa
            for (ElementoCarrello el : carrello) {
                totaleOrdine += (el.getProdotto().getPrezzoBase() + el.getPrezzoExtra()) * el.getQuantita();
            }

            int numeroOrdine = 0;

            // --- FASE 1: SALVATAGGIO NEL DATABASE ---
            try (Connection conn = DBConnect.getConnection()) {
                conn.setAutoCommit(false); 
                
                String sqlOrdine = "INSERT INTO Ordine (id_cliente, orario_consegna_richiesto, stato_attuale, prezzo_totale, tempo_stimato_consegna) VALUES (?, DATE_ADD(NOW(), INTERVAL 40 MINUTE), 'inserito', ?, 40)";
                try (PreparedStatement ps = conn.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.setDouble(2, totaleOrdine);
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            numeroOrdine = rs.getInt(1); 
                        }
                    }
                }
                
                if (numeroOrdine > 0) {
                    String sqlDettaglio = "INSERT INTO Dettaglio_Ordine (id_ordine, id_prodotto, quantita) VALUES (?, ?, ?)";
                    try (PreparedStatement psDett = conn.prepareStatement(sqlDettaglio)) {
                        for (ElementoCarrello el : carrello) {
                            psDett.setInt(1, numeroOrdine);
                            
                            // ECCO LA CORREZIONE: usiamo getIdProdotto() al posto di getId()
                            psDett.setInt(2, el.getProdotto().getIdProdotto()); 
                            
                            psDett.setInt(3, el.getQuantita());
                            psDett.executeUpdate();
                        }
                    }
                    conn.commit(); // Salvataggio definitivo nel Database!
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

            // --- FASE 2: COSTRUZIONE E INVIO EMAIL ---
            StringBuilder html = new StringBuilder();
            html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
            html.append("<h2 style='color: #116C4A;'>Grazie per il tuo ordine, ").append(nome).append("!</h2>");
            html.append("<p>Il tuo ordine <strong>#").append(numeroOrdine).append("</strong> è stato ricevuto.</p>");
            html.append("</div>");

            try {
                // Proviamo a inviare l'email
                EmailUtility.inviaEmailRiepilogo(email, "Conferma Ordine #" + numeroOrdine, html.toString());
            } catch (Throwable t) {
                // SE LE LIBRERIE MAIL MANCANO O ESPLODONO, CATTURIAMO L'ERRORE QUI SENZA BLOCCARE IL SITO
                System.out.println("ATTENZIONE: Email non inviata, ma ordine salvato! Errore: " + t.getMessage());
            }
            
            // Sia che l'email parta, sia che fallisca, SVUOTIAMO IL CARRELLO E DIAMO SUCCESSO
            session.removeAttribute("carrello");
            out.print("{\"successo\": true, \"messaggio\": \"Ordine confermato con successo!\"}");

        } catch (Throwable t) {
            // Questo blocca qualsiasi altro errore cosmico di Java
            t.printStackTrace();
            out.print("{\"successo\": false, \"messaggio\": \"Errore imprevisto del server.\"}");
        }
    }
}