package controller;

import model.Utente;
import util.JpaUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminStaffServlet", urlPatterns = {"/admin/staff"})
public class AdminStaffServlet extends HttpServlet {

    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        freemarker.ext.jakarta.servlet.WebappTemplateLoader templateLoader = 
            new freemarker.ext.jakarta.servlet.WebappTemplateLoader(getServletContext(), "/WEB-INF/templates");
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
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

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            List<Utente> staffList = em.createQuery(
                "SELECT u FROM Utente u WHERE u.ruolo = 'personale' ORDER BY u.nomeCompleto ASC", 
                Utente.class).getResultList();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("staffList", staffList);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_staff.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore nel caricamento dello staff", e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        
        String nome = request.getParameter("nome_completo");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String password = request.getParameter("password");

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            em.getTransaction().begin();
            
            Utente nuovoStaff = new Utente();
            nuovoStaff.setNomeCompleto(nome);
            nuovoStaff.setEmail(email);
            nuovoStaff.setTelefono(telefono);
            nuovoStaff.setPassword(password);
            nuovoStaff.setRuolo("personale"); 
            em.persist(nuovoStaff);
            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new ServletException("Errore durante il salvataggio del nuovo membro dello staff", e);
        } finally {
            em.close();
        }

        response.sendRedirect("staff");
    }
}