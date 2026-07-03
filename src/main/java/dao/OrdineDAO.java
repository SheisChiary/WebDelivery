package dao;

import model.Ordine;
import model.StoricoStatiOrdine;
import model.Utente;
import util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class OrdineDAO {

    public List<Ordine> getOrdiniInCorso() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Ordine o WHERE o.stato NOT IN ('consegnato', 'annullato') ORDER BY o.dataCreazione ASC", Ordine.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ordine> getStoricoOrdini() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Ordine o WHERE o.stato IN ('consegnato', 'annullato') ORDER BY o.dataCreazione DESC", Ordine.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Ordine getOrdineById(Long id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT o FROM Ordine o WHERE o.id = :id", Ordine.class)
                     .setParameter("id", id)
                     .getSingleResult();
        } catch (Exception e) {
            return null; 
        } finally {
            em.close();
        }
    }
 
    public void aggiornaStatoOrdine(Long idOrdine, String nuovoStato, Utente operatore) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Ordine ordine = em.find(Ordine.class, idOrdine);
            
            if (ordine != null && isTransizioneValida(ordine.getStato(), nuovoStato)) {
                ordine.setStato(nuovoStato);
                
                StoricoStatiOrdine storico = new StoricoStatiOrdine();
                storico.setOrdine(ordine);
                storico.setPersonale(operatore);
                storico.setStato(nuovoStato);
                storico.setDataOraModifica(LocalDateTime.now());
                
                em.persist(storico);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
        private boolean isTransizioneValida(String statoAttuale, String nuovoStato) {
        if (statoAttuale.equalsIgnoreCase("inserito") && nuovoStato.equalsIgnoreCase("in preparazione")) return true;
        if (statoAttuale.equalsIgnoreCase("in preparazione") && nuovoStato.equalsIgnoreCase("pronto")) return true;
        if (statoAttuale.equalsIgnoreCase("pronto") && nuovoStato.equalsIgnoreCase("in consegna")) return true;
        if (statoAttuale.equalsIgnoreCase("in consegna") && nuovoStato.equalsIgnoreCase("consegnato")) return true;
        return false;
        }
}