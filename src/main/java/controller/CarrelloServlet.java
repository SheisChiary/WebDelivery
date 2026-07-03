package controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.CartItem;
import model.Prodotto;
import model.Caratteristica;
import util.JpaUtil;

@WebServlet(name = "CarrelloServlet", urlPatterns = {"/carrello", "/aggiungi-carrello"})
public class CarrelloServlet extends HttpServlet {

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
        HttpSession session = request.getSession(true);
        
        List<CartItem> carrello = (List<CartItem>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new ArrayList<>();
            session.setAttribute("carrello", carrello);
        }

        double subtotale = 0.0;
        int tempoPreparazioneBase = 0;
        
        for (CartItem item : carrello) {
            subtotale += (item.getProdotto().getPrezzo() + item.getCostoExtra()) * item.getQuantita();
            tempoPreparazioneBase += item.getProdotto().getTempoPreparazione() * item.getQuantita();
        }

        double costoConsegna = 2.50;
        double totale = subtotale + costoConsegna;
        
        int tempoMinimo = tempoPreparazioneBase + 10;
        int tempoMassimo = tempoPreparazioneBase + 20;

        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        List<Prodotto> bevande = new ArrayList<>();
        Map<String, Map<String, List<Caratteristica>>> customizzazioni = new HashMap<>();
        
        try {
            bevande = em.createQuery("SELECT p FROM Prodotto p WHERE p.categoria = 'Bevande'", Prodotto.class)
                        .setMaxResults(2)
                        .getResultList();

            for (CartItem item : carrello) {
                String pid = String.valueOf(item.getProdotto().getId());
                if (!customizzazioni.containsKey(pid)) {
                    Map<String, List<Caratteristica>> gruppi = new HashMap<>();
                    
                    List<Caratteristica> list = em.createQuery("SELECT c FROM Caratteristica c LEFT JOIN FETCH c.gruppo WHERE c.prodotto.id = :pid", Caratteristica.class)
                                                  .setParameter("pid", item.getProdotto().getId())
                                                  .getResultList();
                    
                    for (Caratteristica c : list) {
                        String key = (c.getGruppo() != null) ? c.getGruppo().getNomeGruppo() : "Extra";
                        gruppi.putIfAbsent(key, new ArrayList<>());
                        gruppi.get(key).add(c);
                    }
                    customizzazioni.put(pid, gruppi);
                }
            }
        } finally {
            em.close();
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("carrello", carrello);
        dataModel.put("subtotale", subtotale);
        dataModel.put("costoConsegna", costoConsegna);
        dataModel.put("totale", totale);
        dataModel.put("tempoMinimo", tempoMinimo);
        dataModel.put("tempoMassimo", tempoMassimo);
        dataModel.put("bevande", bevande);
        dataModel.put("customizzazioni", customizzazioni);

        Boolean ordineCompletato = (Boolean) session.getAttribute("ordineCompletato");
        if (ordineCompletato != null && ordineCompletato) {
            dataModel.put("ordineCompletato", true);
            session.removeAttribute("ordineCompletato");
        }

        try {
            Template template = cfg.getTemplate("carrello.ftl");
            template.process(dataModel, response.getWriter());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        List<CartItem> carrello = (List<CartItem>) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new ArrayList<>();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        String idProdottoStr = request.getParameter("idProdotto");

        if (idProdottoStr != null) {
            Long idProdotto = Long.parseLong(idProdottoStr);
            
            if (action == null || "add".equals(action)) {
                boolean found = false;
                for (CartItem item : carrello) {
                    if (item.getProdotto().getId().equals(idProdotto) && item.getPersonalizzazioni().isEmpty()) {
                        item.setQuantita(item.getQuantita() + 1);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
                    try {
                        Prodotto p = em.find(Prodotto.class, idProdotto);
                        if (p != null) {
                            CartItem newItem = new CartItem(p, 1);
                            
                            List<Caratteristica> defaults = em.createQuery("SELECT c FROM Caratteristica c WHERE c.prodotto.id = :pid AND c.isDefault = true", Caratteristica.class)
                                                              .setParameter("pid", idProdotto)
                                                              .getResultList();
                            
                            double extraCost = 0.0;
                            StringBuilder pers = new StringBuilder();
                            List<Long> scelti = new ArrayList<>();
                            
                            for (Caratteristica c : defaults) {
                                extraCost += c.getDifferenzaPrezzo();
                                if (pers.length() > 0) pers.append(", ");
                                pers.append(c.getNome());
                                scelti.add(c.getId());
                            }
                            newItem.setCostoExtra(extraCost);
                            newItem.setPersonalizzazioni(pers.toString());
                            newItem.setCaratteristicheScelte(scelti);
                            
                            carrello.add(newItem);
                        }
                    } finally {
                        em.close();
                    }
                }
                response.sendRedirect("carrello");
                return;
            } else if ("add_customized".equals(action)) {
                EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
                try {
                    Prodotto p = em.find(Prodotto.class, idProdotto);
                    if (p != null) {
                        CartItem newItem = new CartItem(p, 1);
                        String[] caratteristicheIds = request.getParameterValues("caratteristica");
                        double extraCost = 0.0;
                        StringBuilder pers = new StringBuilder();
                        List<Long> scelti = new ArrayList<>();
                        
                        if (caratteristicheIds != null) {
                            for (String cid : caratteristicheIds) {
                                Long idC = Long.parseLong(cid);
                                scelti.add(idC);
                                Caratteristica c = em.find(Caratteristica.class, idC);
                                if (c != null) {
                                    extraCost += c.getDifferenzaPrezzo();
                                    if (pers.length() > 0) pers.append(", ");
                                    pers.append(c.getNome());
                                }
                            }
                        }
                        newItem.setCostoExtra(extraCost);
                        newItem.setPersonalizzazioni(pers.toString());
                        newItem.setCaratteristicheScelte(scelti);
                        carrello.add(newItem);
                    }
                } finally {
                    em.close();
                }
                response.sendRedirect("carrello");
                return;
            } else if ("remove".equals(action)) {
                carrello.removeIf(item -> item.getProdotto().getId().equals(idProdotto));
            } else if ("decrease".equals(action)) {
                for (CartItem item : carrello) {
                    if (item.getProdotto().getId().equals(idProdotto)) {
                        if (item.getQuantita() > 1) {
                            item.setQuantita(item.getQuantita() - 1);
                        } else {
                            carrello.remove(item);
                        }
                        break;
                    }
                }
            }
        } else if ("customize".equals(action)) {
            String indexStr = request.getParameter("itemIndex");
            String[] caratteristicheIds = request.getParameterValues("caratteristica");
            
            if (indexStr != null) {
                int itemIndex = Integer.parseInt(indexStr);
                if (itemIndex >= 0 && itemIndex < carrello.size()) {
                    CartItem item = carrello.get(itemIndex);
                    double extraCost = 0.0;
                    StringBuilder pers = new StringBuilder();
                    List<Long> scelti = new ArrayList<>();
                    
                    if (caratteristicheIds != null && caratteristicheIds.length > 0) {
                        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
                        try {
                            for (String cid : caratteristicheIds) {
                                Long idC = Long.parseLong(cid);
                                scelti.add(idC);
                                Caratteristica c = em.find(Caratteristica.class, idC);
                                if (c != null) {
                                    extraCost += c.getDifferenzaPrezzo();
                                    if (pers.length() > 0) pers.append(", ");
                                    pers.append(c.getNome());
                                }
                            }
                        } finally {
                            em.close();
                        }
                    }
                    
                    item.setCostoExtra(extraCost);
                    item.setPersonalizzazioni(pers.toString());
                    item.setCaratteristicheScelte(scelti);
                }
            }
        } else if ("clear_customize".equals(action)) {
            String indexStr = request.getParameter("itemIndex");
            if (indexStr != null) {
                int itemIndex = Integer.parseInt(indexStr);
                if (itemIndex >= 0 && itemIndex < carrello.size()) {
                    CartItem item = carrello.get(itemIndex);
                    
                    EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
                    try {
                        List<Caratteristica> defaults = em.createQuery("SELECT c FROM Caratteristica c WHERE c.prodotto.id = :pid AND c.isDefault = true", Caratteristica.class)
                                                          .setParameter("pid", item.getProdotto().getId())
                                                          .getResultList();
                        
                        double extraCost = 0.0;
                        StringBuilder pers = new StringBuilder();
                        List<Long> scelti = new ArrayList<>();
                        for (Caratteristica c : defaults) {
                            extraCost += c.getDifferenzaPrezzo();
                            if (pers.length() > 0) pers.append(", ");
                            pers.append(c.getNome());
                            scelti.add(c.getId());
                        }
                        item.setCostoExtra(extraCost);
                        item.setPersonalizzazioni(pers.toString());
                        item.setCaratteristicheScelte(scelti);
                    } finally {
                        em.close();
                    }
                }
            }
        }
        
        response.sendRedirect("carrello");
    }
}