package controller;

import dao.UtenteDAO;
import model.Utente;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminStaffServlet", urlPatterns = {"/admin/staff"})
public class AdminStaffServlet extends HttpServlet {

    private Configuration cfg;
    private UtenteDAO utenteDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        
        utenteDao = new UtenteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Utente> staffList = utenteDao.getStaffList();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("staffList", staffList);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_staff.ftl");
            template.process(templateData, response.getWriter());
        } catch (Exception e) {
            throw new ServletException("Errore nel caricamento dello staff", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        Utente nuovoStaff = new Utente();
        nuovoStaff.setNomeCompleto(request.getParameter("nome_completo"));
        nuovoStaff.setEmail(request.getParameter("email"));
        nuovoStaff.setTelefono(request.getParameter("telefono"));
        nuovoStaff.setPassword(request.getParameter("password"));
        nuovoStaff.setRuolo("personale"); 
        
        try {
            utenteDao.salvaUtente(nuovoStaff);
        } catch (Exception e) {
            throw new ServletException("Errore durante il salvataggio del nuovo membro dello staff", e);
        }

        response.sendRedirect("staff");
    }
}