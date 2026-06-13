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

            // --- NUOVI PARAMETRI RECUPERATI DAL FRONTEND ---
            String tipoOrario = request.getParameter("tipoOrario");
            String orarioScelto = request.getParameter("orario");
            int tempoStimatoCalcolato = 40; // Tempo di default

            // Calcolo del totale (inclusa eventuale spedizione o costo base)
            double totaleOrdine = 2.50; 
            for (ElementoCarrello el : carrello) {
                totaleOrdine += (el.getProdotto().getPrezzoBase() + el.getPrezzoExtra()) * el.getQuantita();
            }

            int numeroOrdine = 0;

            // Salvataggio nel database ---
            try (Connection conn = DBConnect.getConnection()) {
                conn.setAutoCommit(false); 
                
                // --- LOGICA SQL DINAMICA PER L'ORARIO SCELTO ---
                String espressioneOrarioSQL = "DATE_ADD(NOW(), INTERVAL ? MINUTE)";
                boolean isProgrammato = "prog".equals(tipoOrario) && orarioScelto != null && !orarioScelto.trim().isEmpty();
                
                if (isProgrammato) {
                    // Unisce la data di oggi con l'orario scelto (aggiungendo :00 per i secondi)
                    espressioneOrarioSQL = "CONCAT(CURDATE(), ' ', ?, ':00')"; 
                }

                // 1. Creazione dell'Ordine "Testata"
                String sqlOrdine = "INSERT INTO Ordine (id_cliente, orario_consegna_richiesto, stato_attuale, prezzo_totale, tempo_stimato_consegna) VALUES (?, " + espressioneOrarioSQL + ", 'inserito', ?, ?)";
                
                try (PreparedStatement ps = conn.prepareStatement(sqlOrdine, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente); // Parametro 1
                    
                    if (isProgrammato) {
                        ps.setString(2, orarioScelto); // Parametro 2 (se programmato)
                    } else {
                        ps.setInt(2, tempoStimatoCalcolato); // Parametro 2 (se ASAP)
                    }
                    
                    ps.setDouble(3, totaleOrdine); // Parametro 3
                    ps.setInt(4, tempoStimatoCalcolato); // Parametro 4
                    
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            numeroOrdine = rs.getInt(1); 
                        }
                    }
                }
                
                if (numeroOrdine > 0) {
                    // 2. Inserimento dei prodotti del carrello
                    String sqlDettaglio = "INSERT INTO Dettaglio_Ordine (id_ordine, id_prodotto, quantita) VALUES (?, ?, ?)";
                    try (PreparedStatement psDett = conn.prepareStatement(sqlDettaglio)) {
                        for (ElementoCarrello el : carrello) {
                            psDett.setInt(1, numeroOrdine);
                            psDett.setInt(2, el.getProdotto().getIdProdotto()); 
                            psDett.setInt(3, el.getQuantita());
                            psDett.executeUpdate();
                        }
                    }
          
                    // 3. Salvataggio Storico Stati Ordine
                    String sqlStorico = "INSERT INTO Storico_Stati_Ordine (id_ordine, id_personale, stato) VALUES (?, ?, 'inserito')";
                    try (PreparedStatement psStorico = conn.prepareStatement(sqlStorico)) {
                        psStorico.setInt(1, numeroOrdine);
                        psStorico.setInt(2, idCliente); 
                        psStorico.executeUpdate();
                    }
                    
                    conn.commit(); // Salvataggio definitivo
                    
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

            // Invia l'email di conferma
            StringBuilder html = new StringBuilder();
            html.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>");
            html.append("<h2 style='color: #116C4A;'>Grazie per il tuo ordine, ").append(nome).append("!</h2>");
            html.append("<p>Il tuo ordine <strong>#").append(numeroOrdine).append("</strong> è stato ricevuto e sarà in preparazione a breve.</p>");
            html.append("</div>");

            try {
                EmailUtility.inviaEmailRiepilogo(email, "Conferma Ordine #" + numeroOrdine, html.toString());
            } catch (Throwable t) {
                System.out.println("ATTENZIONE: Email non inviata, ma ordine salvato! Errore: " + t.getMessage());
            }
            
            session.removeAttribute("carrello");
            out.print("{\"successo\": true, \"messaggio\": \"Ordine confermato con successo!\"}");

        } catch (Throwable t) {
            t.printStackTrace();
            out.print("{\"successo\": false, \"messaggio\": \"Errore imprevisto del server.\"}");
        }
    }
}