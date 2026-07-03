package controller;

import model.Prodotto;
import model.Caratteristica;
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
import java.util.ArrayList;

@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends HttpServlet {
    
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
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            List<Prodotto> catalogo = em.createQuery("SELECT p FROM Prodotto p", Prodotto.class).getResultList();

            HttpSession session = request.getSession(false);
            String nomeUtente = "";
            if (session != null && session.getAttribute("utente_nome") != null) {
                nomeUtente = (String) session.getAttribute("utente_nome");
            }

            Map<String, Map<String, List<Caratteristica>>> customizzazioni = new HashMap<>();
            for (Prodotto p : catalogo) {
                String pid = String.valueOf(p.getId());
                Map<String, List<Caratteristica>> gruppi = new HashMap<>();
                
                List<Caratteristica> list = em.createQuery("SELECT c FROM Caratteristica c LEFT JOIN FETCH c.gruppo WHERE c.prodotto.id = :pid", Caratteristica.class)
                                              .setParameter("pid", p.getId())
                                              .getResultList();
                
                for (Caratteristica c : list) {
                    String key = (c.getGruppo() != null) ? c.getGruppo().getNomeGruppo() : "Extra";
                    gruppi.putIfAbsent(key, new ArrayList<>());
                    gruppi.get(key).add(c);
                }
                customizzazioni.put(pid, gruppi);
            }

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("prodotti", catalogo);
            templateData.put("nomeUtente", nomeUtente);
            templateData.put("customizzazioni", customizzazioni);

            Template template = cfg.getTemplate("menu.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }
}