package control;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet("/debug")
public class DebugServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false); // Non crearne una nuova

        out.println("<h1>Stato Sessione:</h1>");
        if (session == null) {
            out.println("<p>NESSUNA SESSIONE ATTIVA</p>");
        } else {
            Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                out.println("<p><b>" + name + "</b>: " + session.getAttribute(name) + "</p>");
            }
        }
    }
}