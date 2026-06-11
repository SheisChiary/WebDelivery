package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DBConnect;

@WebServlet(name = "AggiornaStatoServlet", urlPatterns = {"/AggiornaStatoServlet"})
public class AggiornaStatoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Controllo di sicurezza
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_utente") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Recupero l'ID dello staff dalla sessione 
        int idPersonale = (int) session.getAttribute("id_utente");
        
        // Recupero i dati inviati dal bottone HTML
        String idOrdineStr = request.getParameter("id_ordine");
        String nuovoStato = request.getParameter("nuovo_stato");

        if (idOrdineStr == null || nuovoStato == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int idOrdine = Integer.parseInt(idOrdineStr);

        try (Connection conn = DBConnect.getConnection()) {
            
           
            conn.setAutoCommit(false);

            try {
                // Aggiorno Tabella Ordine
                String updateOrdine = "UPDATE Ordine SET stato_attuale = ? WHERE id_ordine = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(updateOrdine)) {
                    ps1.setString(1, nuovoStato);
                    ps1.setInt(2, idOrdine);
                    ps1.executeUpdate();
                }

                // Lascio la "firma" di chi ha fatto l'azione (Tabella Storico)
                String insertStorico = "INSERT INTO Storico_Stati_Ordine (id_ordine, id_personale, stato) VALUES (?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insertStorico)) {
                    ps2.setInt(1, idOrdine);
                    ps2.setInt(2, idPersonale);
                    ps2.setString(3, nuovoStato);
                    ps2.executeUpdate();
                }

                // Confermo le modifiche nel database
                conn.commit();
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}