package controller;

import dao.UtenteDAO;
import freemarker.template.Configuration;
import freemarker.template.Template;
import model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminStaffModificaServlet", urlPatterns = {"/admin/staff-modifica"})
public class AdminStaffModificaServlet extends HttpServlet {

    private Configuration cfg;
    private UtenteDAO utenteDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        
        utenteDao = new UtenteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("staff");
            return;
        }

        try {
            Utente dipendente = utenteDao.getUtenteById(Long.parseLong(idParam));
            
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("dipendente", dipendente);
            templateData.put("utenteLoggato", utenteLoggato);
            
            Template template = cfg.getTemplate("admin_staff_modifica.ftl");
            template.process(templateData, response.getWriter());
            
        } catch (Exception e) {
            throw new ServletException("Errore", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Long idUtente = Long.parseLong(request.getParameter("id"));
        String nome = request.getParameter("nome_completo");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String nuovaPassword = request.getParameter("password");

        try {
            utenteDao.aggiornaStaff(idUtente, nome, email, telefono, nuovaPassword);
        } catch (Exception e) {
            throw new ServletException("Errore update", e);
        }

        response.sendRedirect("staff");
    }
}