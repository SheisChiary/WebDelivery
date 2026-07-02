package controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import model.Utente;
import util.JpaUtil;

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
            dataModel.put("errore", "Credenziali non valide o utente non trovato nel DB!");
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

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            Utente utente = em.createQuery("SELECT u FROM Utente u WHERE u.email = :email AND u.password = :password", Utente.class)
                              .setParameter("email", email)
                              .setParameter("password", password)
                              .getSingleResult();
            
            HttpSession session = request.getSession(true);
            session.setAttribute("utente_id", utente.getId());
            session.setAttribute("utente_ruolo", utente.getRuolo());
            session.setAttribute("utente_nome", utente.getNomeCompleto()); 
            
            String ruolo = utente.getRuolo();
            
            if (ruolo != null && ruolo.equalsIgnoreCase("proprietario")) {
                response.sendRedirect("admin_ordine");
            } else if (ruolo != null && ruolo.equalsIgnoreCase("personale")) {
                response.sendRedirect("staff_ordine");
            } else {
                response.sendRedirect("menu");
            }
            
        } catch (NoResultException e) {
            response.sendRedirect("login?error=1");
        } catch (Exception e) {
            response.sendRedirect("login?error=1");
        } finally {
            em.close();
        }
    }
}