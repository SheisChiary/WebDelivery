package controller;
import model.Prodotto;
import util.JpaUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("prodotti", catalogo);

            Template template = cfg.getTemplate("menu.ftl");
            template.process(templateData, response.getWriter());

        } catch (Exception e) {
            throw new ServletException("Errore durante il caricamento del menù", e);
        } finally {
            em.close();
        }
    }
}