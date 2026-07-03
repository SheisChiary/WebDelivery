package dao;

import model.Ordine;
import model.StoricoStatiOrdine;
import model.Utente;
import util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Query;
import model.CartItem;

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
        
        public List<Object[]> getStoricoOrdiniCliente(Long idCliente) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createNativeQuery(
                "SELECT id_ordine, data_creazione, stato_attuale, prezzo_totale, tempo_stimato_consegna FROM Ordine WHERE id_cliente = ? ORDER BY data_creazione DESC")
                .setParameter(1, idCliente)
                .getResultList();
        } finally {
            em.close();
        }
    }
        
        public Long salvaNuovoOrdine(Long idCliente, LocalDateTime orarioConsegna, double totale, int tempoStimato, List<CartItem> carrello) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            Query q = em.createNativeQuery("INSERT INTO Ordine (id_cliente, orario_consegna_richiesto, stato_attuale, prezzo_totale, tempo_stimato_consegna) VALUES (?, ?, 'inserito', ?, ?)");
            q.setParameter(1, idCliente);
            q.setParameter(2, orarioConsegna);
            q.setParameter(3, totale);
            q.setParameter(4, tempoStimato);
            q.executeUpdate();

            Number idOrdine = (Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();

            for (CartItem item : carrello) {
                Query qDet = em.createNativeQuery("INSERT INTO Dettaglio_Ordine (id_ordine, id_prodotto, quantita, prezzo_unitario) VALUES (?, ?, ?, ?)");
                qDet.setParameter(1, idOrdine.longValue());
                qDet.setParameter(2, item.getProdotto().getId());
                qDet.setParameter(3, item.getQuantita());
                qDet.setParameter(4, item.getProdotto().getPrezzo() + item.getCostoExtra());
                qDet.executeUpdate();

                Number idDettaglio = (Number) em.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();

                if (item.getCaratteristicheScelte() != null) {
                    for (Long idCaratt : item.getCaratteristicheScelte()) {
                        Query qCar = em.createNativeQuery("INSERT INTO Dettaglio_Ordine_Caratteristiche (id_dettaglio, id_caratteristica) VALUES (?, ?)");
                        qCar.setParameter(1, idDettaglio.longValue());
                        qCar.setParameter(2, idCaratt);
                        qCar.executeUpdate();
                    }
                }
            }
            em.getTransaction().commit();
            return idOrdine.longValue();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}