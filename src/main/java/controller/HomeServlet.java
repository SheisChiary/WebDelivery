package controller;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    
    private Configuration cfg;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
    }

@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        jakarta.persistence.EntityManager em = util.JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            java.util.List<model.Prodotto> catalogo = em.createQuery("SELECT p FROM Prodotto p", model.Prodotto.class).getResultList();

            java.util.Map<String, Object> templateData = new java.util.HashMap<>();
            templateData.put("prodotti", catalogo);

            freemarker.template.Template template = cfg.getTemplate("home.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore durante il caricamento dei prodotti in Home", e);
        } finally {
            em.close();
        }
    }
}