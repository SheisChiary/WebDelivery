package controller;

import dao.UtenteDAO;
import model.Utente;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        Map<String, Object> dataModel = new HashMap<>();
        
        if (request.getParameter("error") != null) {
            dataModel.put("errore", "Email o Password sbagliate. Riprova!");
        }

        try {
            Template template = cfg.getTemplate("login.ftl");
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new ServletException("Errore nel caricamento del template", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UtenteDAO dao = new UtenteDAO();
        Utente utente = dao.getUtenteByEmailPassword(email, password);
        
        if (utente != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);
            
            String ruolo = utente.getRuolo();
            
            if (ruolo != null && ruolo.equalsIgnoreCase("proprietario")) {
                response.sendRedirect(request.getContextPath() + "/admin/ordini");
            } else if (ruolo != null && ruolo.equalsIgnoreCase("personale")) {
                response.sendRedirect(request.getContextPath() + "/staff/ordini-live"); 
            } else {
                response.sendRedirect(request.getContextPath() + "/menu");
            }
        } else {
            response.sendRedirect("login?error=1");
        }
    }
}