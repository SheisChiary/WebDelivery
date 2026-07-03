package controller;

import dao.ProdottoDAO;
import model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AdminMenuEliminaServlet", urlPatterns = {"/admin/menu-elimina"})
public class AdminMenuEliminaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                ProdottoDAO dao = new ProdottoDAO();
                dao.eliminaProdotto(Long.parseLong(idParam));
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }

        response.sendRedirect("menu");
    }
}