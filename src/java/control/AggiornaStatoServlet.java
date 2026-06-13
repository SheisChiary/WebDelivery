package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.DBConnect;
import utils.EmailUtility;

@WebServlet(name = "AggiornaStatoServlet", urlPatterns = {"/AggiornaStatoServlet"})
public class AggiornaStatoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_utente") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int idPersonale = (int) session.getAttribute("id_utente");
        String idOrdineStr = request.getParameter("id_ordine");
        String nuovoStato = request.getParameter("nuovo_stato");

        if (idOrdineStr == null || nuovoStato == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int idOrdine = Integer.parseInt(idOrdineStr);
        
        // Usiamo variabili temporanee perché poi dovranno essere rese "final" per il thread
        String emailClienteTemp = null;
        String nomeClienteTemp = null;

        try (Connection conn = DBConnect.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Aggiorno Tabella Ordine
                String updateOrdine = "UPDATE Ordine SET stato_attuale = ? WHERE id_ordine = ?";
                try (PreparedStatement ps1 = conn.prepareStatement(updateOrdine)) {
                    ps1.setString(1, nuovoStato);
                    ps1.setInt(2, idOrdine);
                    ps1.executeUpdate();
                }

                // 2. Storico di chi ha fatto l'azione
                String insertStorico = "INSERT INTO Storico_Stati_Ordine (id_ordine, id_personale, stato) VALUES (?, ?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insertStorico)) {
                    ps2.setInt(1, idOrdine);
                    ps2.setInt(2, idPersonale);
                    ps2.setString(3, nuovoStato);
                    ps2.executeUpdate();
                }

                // 3. RECUPERO DATI CLIENTE PER L'EMAIL
                String sqlUtente = "SELECT u.email, u.nome_completo FROM Ordine o JOIN Utente u ON o.id_cliente = u.id_utente WHERE o.id_ordine = ?";
                try (PreparedStatement ps3 = conn.prepareStatement(sqlUtente)) {
                    ps3.setInt(1, idOrdine);
                    try (ResultSet rs = ps3.executeQuery()) {
                        if (rs.next()) {
                            emailClienteTemp = rs.getString("email");
                            nomeClienteTemp = rs.getString("nome_completo");
                        }
                    }
                }

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
            return;
        }

        // --- INVIO DELLE EMAIL AUTOMATICHE IN BASE ALLO STATO ---
        // IN JAVA: Le variabili usate nei Thread devono essere dichiarate 'final'
        final String emailCliente = emailClienteTemp;
        final String nomeCliente = nomeClienteTemp;
        final int finalIdOrdine = idOrdine;

        if (emailCliente != null) {
            if ("in consegna".equalsIgnoreCase(nuovoStato.trim())) {
                final String subject = "🛵 Il tuo ordine #" + finalIdOrdine + " è in arrivo!";
                final String html = "<div style='font-family: Arial, sans-serif; text-align: center; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; padding: 30px; border-radius: 12px;'>" +
                              "<h2 style='color: #D97706;'>Preparati, " + nomeCliente + "!</h2>" +
                              "<p style='font-size: 16px; color: #555;'>Il tuo ordine <strong>#" + finalIdOrdine + "</strong> è stato appena affidato al nostro rider ed è in viaggio verso di te.</p>" +
                              "<p style='font-size: 18px; font-weight: bold; color: #333;'>Tieni d'occhio il citofono! 🚀</p></div>";
                
                new Thread(() -> { EmailUtility.inviaEmailRiepilogo(emailCliente, subject, html); }).start();
                
            } else if ("consegnato".equalsIgnoreCase(nuovoStato.trim())) {
                final String subject = "✅ Ordine #" + finalIdOrdine + " consegnato!";
                final String html = "<div style='font-family: Arial, sans-serif; text-align: center; max-width: 600px; margin: 0 auto; border: 1px solid #e5e7eb; padding: 30px; border-radius: 12px;'>" +
                              "<h2 style='color: #116C4A;'>Buon appetito, " + nomeCliente + "!</h2>" +
                              "<p style='font-size: 16px; color: #555;'>Il tuo ordine <strong>#" + finalIdOrdine + "</strong> risulta consegnato con successo.</p>" +
                              "<p style='font-size: 16px; color: #555;'>Speriamo sia stato tutto di tuo gradimento. Grazie per aver scelto WebDelivery!</p></div>";
                
                new Thread(() -> { EmailUtility.inviaEmailRiepilogo(emailCliente, subject, html); }).start();
            }
        }
    }
}