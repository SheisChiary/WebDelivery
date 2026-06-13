package control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DBConnect;


@WebServlet(name = "DeleteStaffServlet", urlPatterns = {"/DeleteStaffServlet"})
public class DeleteStaffServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Elimina solo se è davvero un membro dello staff 
        String sql = "DELETE FROM Utente WHERE id_utente = ? AND ruolo = 'personale'";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, Integer.parseInt(idStr));
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Eliminato con successo
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Utente non trovato
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}