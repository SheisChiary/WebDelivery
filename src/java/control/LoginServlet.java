package control;

import model.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "SELECT id_utente, nome_completo, ruolo FROM Utente WHERE email = ? AND password = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_utente");
                    String nome = rs.getString("nome_completo");
                    String ruolo = rs.getString("ruolo");
                    
                    // Pulizia del ruolo per evitare errori (spazi o maiuscole)
                    String ruoloPulito = (ruolo != null) ? ruolo.trim().toLowerCase() : "";
                    
                    HttpSession session = request.getSession();
                    session.setAttribute("id_utente", id);
                    session.setAttribute("nome_completo", nome);
                    session.setAttribute("ruolo", ruoloPulito);
                    
                    // Logica di reindirizzamento basata sul ruolo
                    if (ruoloPulito.equals("proprietario")) {
                        response.sendRedirect("dashboard_admin.html");
                    } else if (ruoloPulito.equals("personale")) {
                        response.sendRedirect("dashboard_staff.html");
                    } else {
                        response.sendRedirect("dashboard.html");
                    }
                    
                } else {
                    // Login fallito: pagina di errore
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<html><body style='font-family: Arial; text-align: center; margin-top: 50px; background-color: #f8f9fa;'>");
                    out.println("<div style='background-color: white; padding: 40px; border-radius: 12px; border: 1px solid #eee; display: inline-block; box-shadow: 0 4px 15px rgba(0,0,0,0.06);'>");
                    out.println("<h2 style='color: #e74c3c; margin-bottom: 10px; font-size: 26px;'>Errore: Credenziali non valide!</h2>");
                    out.println("<p style='color: #666;'>Email o password errati.</p>");
                    out.println("<br><a href='login.html' style='color: #116C4A; text-decoration: none; font-weight: bold; margin-top: 15px; display: inline-block;'>← Riprova</a>");
                    out.println("</div></body></html>");
                }
            }
        } catch (SQLException e) {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<h3>Errore Database:</h3> <p>" + e.getMessage() + "</p>");
        }
    }
}
