package controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import model.Utente;
import util.JpaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AdminStaffModificaServlet", urlPatterns = {"/admin/staff-modifica"})
public class AdminStaffModificaServlet extends HttpServlet {

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

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            Utente dipendente = em.find(Utente.class, Long.parseLong(idParam));
            
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("dipendente", dipendente);
            templateData.put("utenteLoggato", utenteLoggato);
            
            Template template = cfg.getTemplate("admin_staff_modifica.ftl");
            template.process(templateData, response.getWriter());
            
        } catch (Exception e) {
            throw new ServletException("Errore", e);
        } finally {
            em.close();
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

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            Utente utenteDaModificare = em.find(Utente.class, idUtente);
            
            if (utenteDaModificare != null) {
                utenteDaModificare.setNomeCompleto(nome);
                utenteDaModificare.setEmail(email);
                utenteDaModificare.setTelefono(telefono);
                
                if (nuovaPassword != null && !nuovaPassword.trim().isEmpty()) {
                    utenteDaModificare.setPassword(nuovaPassword);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ServletException("Errore update", e);
        } finally {
            em.close();
        }

        response.sendRedirect("staff");
    }
}