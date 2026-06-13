package control;

import model.DBConnect;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "EditProdottoServlet", urlPatterns = {"/EditProdottoServlet"})
public class EditProdottoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Recupero i dati dal form della modale di modifica
        String idStr = request.getParameter("id");
        String nome = request.getParameter("nome");
        String categoria = request.getParameter("categoria");
        String prezzoStr = request.getParameter("prezzo");
        String badge = request.getParameter("badge");
        String descrizione = request.getParameter("descrizione");
        String immagine = request.getParameter("immagine");

        // Dati delle varianti/ingredienti modificati
        String[] varNomi = request.getParameterValues("var_nome");
        String[] varPrezzi = request.getParameterValues("var_prezzo");

        int idProdotto = Integer.parseInt(idStr);
        double prezzo = Double.parseDouble(prezzoStr);

        // Query per aggiornare il prodotto base
        String sqlUpdateProdotto = "UPDATE Prodotto SET nome = ?, categoria = ?, prezzo_base = ?, badge = ?, descrizione = ?, ingredienti = ?, immagine_url = ? WHERE id_prodotto = ?";

        try (Connection conn = DBConnect.getConnection()) {
            // Disabilitiamo l'autocommit per fare una "Transazione": o si salva tutto o niente
            conn.setAutoCommit(false);

            try {
                // A. Aggiorno il prodotto
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateProdotto)) {
                    ps.setString(1, nome);
                    ps.setString(2, categoria);
                    ps.setDouble(3, prezzo);
                    ps.setString(4, (badge != null && !badge.trim().isEmpty()) ? badge : null);
                    ps.setString(5, descrizione);
                    ps.setString(6, descrizione);
                    ps.setString(7, (immagine != null && !immagine.trim().isEmpty()) ? immagine : null);
                    ps.setInt(8, idProdotto);
                    ps.executeUpdate();
                }

                // B. Cancello le vecchie varianti per questo piatto (Facciamo pulizia prima di reinserire)
                String sqlDeleteVarianti = "DELETE FROM Caratteristica WHERE id_prodotto = ? AND id_gruppo IS NULL";
                try (PreparedStatement psDel = conn.prepareStatement(sqlDeleteVarianti)) {
                    psDel.setInt(1, idProdotto);
                    psDel.executeUpdate();
                }

                // C. Inserisco le nuove varianti aggiornate
                if (varNomi != null && varPrezzi != null && varNomi.length == varPrezzi.length) {
                    String sqlInsertVariante = "INSERT INTO Caratteristica (id_prodotto, id_gruppo, nome, differenza_prezzo, is_default) VALUES (?, NULL, ?, ?, FALSE)";
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsertVariante)) {
                        for (int i = 0; i < varNomi.length; i++) {
                            if (varNomi[i] != null && !varNomi[i].trim().isEmpty()) {
                                double pVar = 0;
                                try { pVar = Double.parseDouble(varPrezzi[i]); } catch (Exception e) {}

                                psIns.setInt(1, idProdotto);
                                psIns.setString(2, varNomi[i]);
                                psIns.setDouble(3, pVar);
                                psIns.executeUpdate();
                            }
                        }
                    }
                }

                // Se tutto è andato a buon fine, salviamo nel DB definitivamente
                conn.commit();
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (Exception e) {
                // Se qualcosa fallisce, annulliamo le modifiche per non corrompere il DB
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore Database in modifica: " + e.getMessage());
        }
    }
}