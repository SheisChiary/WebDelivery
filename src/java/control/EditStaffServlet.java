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

@WebServlet("/EditStaffServlet")
public class EditStaffServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        boolean updatePassword = (password != null && !password.trim().isEmpty());
        
        String sql;
        if (updatePassword) {
            sql = "UPDATE Utente SET nome_completo = ?, email = ?, password = ? WHERE id_utente = ? AND ruolo = 'personale'";
        } else {
            sql = "UPDATE Utente SET nome_completo = ?, email = ? WHERE id_utente = ? AND ruolo = 'personale'";
        }

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, email);
            
            if (updatePassword) {
                ps.setString(3, password);
                ps.setInt(4, Integer.parseInt(idStr));
            } else {
                ps.setInt(3, Integer.parseInt(idStr));
            }
            
            ps.executeUpdate();
            response.setStatus(HttpServletResponse.SC_OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}