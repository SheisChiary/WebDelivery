package controller;

import dao.UtenteDAO;
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
import java.util.Map;
import model.Utente;

@WebServlet(name = "ProfiloServlet", urlPatterns = {"/profilo"})
public class ProfiloServlet extends HttpServlet {

    private Configuration cfg;
    private UtenteDAO utenteDao;

    @Override
    public void init() throws ServletException {
        cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        cfg.setDefaultEncoding("UTF-8");
        
        utenteDao = new UtenteDAO();
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
            Utente utente = utenteDao.getUtenteById(idUtente);

            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("utente", utente);

            if (request.getParameter("success") != null) {
                dataModel.put("success_msg", "Profilo aggiornato con successo!");
            }

            Template template = cfg.getTemplate("profilo.ftl");
            template.process(dataModel, response.getWriter());

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Utente utenteLoggato = (session != null) ? (Utente) session.getAttribute("utente") : null;
        if (utenteLoggato == null) {
            response.sendRedirect("login");
            return;
        }

        Long idUtente = utenteLoggato.getId();
        String nomeCompleto = request.getParameter("nomeCompleto");
        String telefono = request.getParameter("telefono");
        String indirizzo = request.getParameter("indirizzo");

        try {
            utenteDao.aggiornaProfilo(idUtente, nomeCompleto, telefono, indirizzo);
            
            utenteLoggato.setNomeCompleto(nomeCompleto);
            utenteLoggato.setTelefono(telefono);
            utenteLoggato.setIndirizzo(indirizzo);
            session.setAttribute("utente", utenteLoggato);
            
            response.sendRedirect("profilo?success=1");
        } catch (Exception e) {
            throw new ServletException("Errore nell'aggiornamento del profilo", e);
        }
    }
}