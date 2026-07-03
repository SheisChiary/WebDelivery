package controller;

import model.Prodotto;
import model.Caratteristica;
import model.GruppoEsclusione;
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

@WebServlet(name = "AdminMenuModificaServlet", urlPatterns = {"/admin/menu-modifica"})
public class AdminMenuModificaServlet extends HttpServlet {

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

        Long idProdotto = Long.parseLong(request.getParameter("id"));
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        
        try {
            Prodotto prodotto = em.find(Prodotto.class, idProdotto);
            List<Caratteristica> caratteristiche = em.createQuery(
                "SELECT c FROM Caratteristica c WHERE c.prodotto.id = :pid", Caratteristica.class)
                .setParameter("pid", idProdotto).getResultList();
            
            List<GruppoEsclusione> gruppi = em.createQuery("SELECT g FROM GruppoEsclusione g", GruppoEsclusione.class).getResultList();

            Map<String, Object> templateData = new HashMap<>();
            templateData.put("prodotto", prodotto);
            templateData.put("caratteristiche", caratteristiche);
            templateData.put("gruppi", gruppi);
            templateData.put("utenteLoggato", utenteLoggato);

            Template template = cfg.getTemplate("admin_menu_modifica.ftl");
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

        Long idProdotto = Long.parseLong(request.getParameter("id"));
        String subAction = request.getParameter("subAction");

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            if ("update_prodotto".equals(subAction)) {
                Prodotto p = em.find(Prodotto.class, idProdotto);
                p.setNome(request.getParameter("nome"));
                p.setDescrizione(request.getParameter("descrizione"));
                p.setCategoria(request.getParameter("categoria"));
                p.setImmagine(request.getParameter("immagine"));
                p.setBadge(request.getParameter("badge"));

                String prezzoStr = request.getParameter("prezzo");
                if (prezzoStr != null && !prezzoStr.trim().isEmpty()) {
                    p.setPrezzo(Double.parseDouble(prezzoStr.replace(",", ".")));
                }

                String tempoStr = request.getParameter("tempo_preparazione");
                if (tempoStr != null && !tempoStr.trim().isEmpty()) {
                    p.setTempoPreparazione(Integer.parseInt(tempoStr));
                }
                
            } else if ("add_caratteristica".equals(subAction)) {
                Prodotto p = em.find(Prodotto.class, idProdotto);
                Caratteristica c = new Caratteristica();
                c.setNome(request.getParameter("caratt_nome"));
                
                String carattPrezzoStr = request.getParameter("caratt_prezzo");
                if (carattPrezzoStr != null && !carattPrezzoStr.trim().isEmpty()) {
                    c.setDifferenzaPrezzo(Double.parseDouble(carattPrezzoStr.replace(",", ".")));
                } else {
                    c.setDifferenzaPrezzo(0.0);
                }

                c.setIsDefault(request.getParameter("caratt_default") != null);
                c.setProdotto(p);

                String idGruppoStr = request.getParameter("id_gruppo");
                if (idGruppoStr != null && !idGruppoStr.isEmpty()) {
                    c.setGruppo(em.find(GruppoEsclusione.class, Long.parseLong(idGruppoStr)));
                }
                em.persist(c);

            } else if ("delete_caratteristica".equals(subAction)) {
                Long idCaratt = Long.parseLong(request.getParameter("id_caratteristica"));
                Caratteristica c = em.find(Caratteristica.class, idCaratt);
                if (c != null) {
                    em.remove(c);
                }
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ServletException(e);
        } finally {
            em.close();
        }

        response.sendRedirect("menu-modifica?id=" + idProdotto);
    }
}