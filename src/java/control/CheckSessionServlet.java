package control;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("nome_completo") != null) {
            String nome = (String) session.getAttribute("nome_completo");
            String ruolo = (String) session.getAttribute("ruolo"); // RECUPERIAMO IL RUOLO!
            
            // Per sicurezza, se il ruolo è nullo lo impostiamo a cliente
            if (ruolo == null) {
                ruolo = "cliente";
            }

            // Ora restituiamo anche il ruolo nel JSON
            out.print("{\"loggato\": true, \"nome\": \"" + nome + "\", \"ruolo\": \"" + ruolo + "\"}");
        } else {
            out.print("{\"loggato\": false}");
        }
    }
}