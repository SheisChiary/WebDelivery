package controller;

import dao.OrdineDAO;
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

@WebServlet(name = "StoricoOrdiniServlet", urlPatterns = {"/storico-ordini"})
public class StoricoOrdiniServlet extends HttpServlet {

    private Configuration cfg;
    private OrdineDAO ordineDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        
        ordineDao = new OrdineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        
        Utente utenteLoggato = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utenteLoggato == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            Long idUtente = utenteLoggato.getId();
            List<Object[]> ordini = ordineDao.getStoricoOrdiniCliente(idUtente);
            
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("ordini", ordini);
            
            Template template = cfg.getTemplate("storico_ordini.ftl");
            template.process(dataModel, response.getWriter());
            
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}