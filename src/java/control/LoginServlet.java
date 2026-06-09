package control;

import model.DBConnect;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
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
                    
                    HttpSession session = request.getSession();
                    session.setAttribute("id_utente", id);
                    session.setAttribute("nome_completo", nome);
                    session.setAttribute("ruolo", ruolo);
                    
                    out.println("<html><body style='font-family: Arial; text-align: center; margin-top: 50px; background-color: #f7f9f8;'>");
                    out.println("<div style='background-color: white; padding: 40px; border-radius: 20px; display: inline-block; box-shadow: 0 10px 30px rgba(0,0,0,0.1);'>");
                    out.println("<h2 style='color: #00B862;'>Login effettuato con successo!</h2>");
                    out.println("<h3>Benvenuto, " + nome + " \uD83D\uDC4B</h3>");
                    out.println("<p>Il tuo ruolo nel sistema è: <b>" + ruolo + "</b></p>");
                    out.println("<br><a href='index.html' style='color: white; background-color: #00B862; padding: 15px 25px; text-decoration: none; border-radius: 12px; font-weight: bold; display: inline-block; margin-top: 20px;'>Torna alla Home</a>");
                    out.println("</div></body></html>");
                    
                } else {
                    out.println("<html><body style='font-family: Arial; text-align: center; margin-top: 50px; background-color: #f7f9f8;'>");
                    out.println("<div style='background-color: white; padding: 40px; border-radius: 20px; display: inline-block; box-shadow: 0 10px 30px rgba(0,0,0,0.1);'>");
                    out.println("<h2 style='color: #e74c3c;'>Errore: Credenziali non valide!</h2>");
                    out.println("<p>Email o password errati.</p>");
                    out.println("<br><a href='login.html' style='color: #00B862; text-decoration: none; font-weight: bold;'>← Riprova</a>");
                    out.println("</div></body></html>");
                }
            }

        } catch (SQLException e) {
            out.println("<h3>Errore Database:</h3> <p>" + e.getMessage() + "</p>");
        }
    }
}