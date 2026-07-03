package controller;

import model.Prodotto;
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

@WebServlet(name = "AdminMenuServlet", urlPatterns = {"/admin/menu"})
public class AdminMenuServlet extends HttpServlet {

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
            List<Prodotto> menu = em.createQuery("SELECT p FROM Prodotto p ORDER BY p.categoria, p.nome", Prodotto.class).getResultList();
            
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("menu", menu);
            templateData.put("utenteLoggato", utenteLoggato);

            if (request.getParameter("success") != null) {
                templateData.put("successMessage", "Prodotto aggiunto al menù con successo!");
            } else if (request.getParameter("error") != null) {
                templateData.put("errorMessage", "Errore nel salvataggio. Controlla che i numeri inseriti siano corretti.");
            }

            Template template = cfg.getTemplate("admin_menu.ftl");
            template.process(templateData, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");
        if (utenteLoggato == null || !utenteLoggato.getRuolo().equals("proprietario")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            String nome = request.getParameter("nome");
            String descrizione = request.getParameter("descrizione");
            String categoria = request.getParameter("categoria");
            String immagine = request.getParameter("immagine");
            String badge = request.getParameter("badge");

            String prezzoStr = request.getParameter("prezzo");
            double prezzo = 0.0;
            if (prezzoStr != null && !prezzoStr.trim().isEmpty()) {
                prezzo = Double.parseDouble(prezzoStr.replace(",", "."));
            }

            String tempoStr = request.getParameter("tempo_preparazione");
            int tempoPreparazione = 0;
            if (tempoStr != null && !tempoStr.trim().isEmpty()) {
                tempoPreparazione = Integer.parseInt(tempoStr);
            }

            em.getTransaction().begin();
            Prodotto p = new Prodotto();
            p.setNome(nome);
            p.setPrezzo(prezzo);
            p.setDescrizione(descrizione);
            p.setCategoria(categoria);
            p.setImmagine(immagine);
            p.setTempoPreparazione(tempoPreparazione);
            p.setBadge(badge);
            
            em.persist(p);
            em.getTransaction().commit();
            
            response.sendRedirect("menu?success=1");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            response.sendRedirect("menu?error=1");
        } finally {
            em.close();
        }
    }
}