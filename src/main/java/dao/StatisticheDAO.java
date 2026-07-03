package dao;

import util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class StatisticheDAO {

    public Double getIncassoGiornaliero(LocalDate data) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT SUM(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato' AND o.dataCreazione >= :inizio AND o.dataCreazione < :fine", Double.class)
                     .setParameter("inizio", data.atStartOfDay())
                     .setParameter("fine", data.plusDays(1).atStartOfDay())
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Double getIncassoMensile(LocalDate data) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT SUM(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato' AND o.dataCreazione >= :inizio AND o.dataCreazione < :fine", Double.class)
                     .setParameter("inizio", data.withDayOfMonth(1).atStartOfDay())
                     .setParameter("fine", data.plusMonths(1).withDayOfMonth(1).atStartOfDay())
                     .getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<Object[]> getTopProdotti() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT d.prodotto.nome, SUM(d.quantita) as totale FROM DettaglioOrdine d JOIN d.ordine o WHERE o.stato = 'consegnato' GROUP BY d.prodotto.nome ORDER BY totale DESC", Object[].class)
                     .setMaxResults(5)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Object[]> getFlopProdotti() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT d.prodotto.nome, SUM(d.quantita) as totale FROM DettaglioOrdine d JOIN d.ordine o WHERE o.stato = 'consegnato' GROUP BY d.prodotto.nome ORDER BY totale ASC", Object[].class)
                     .setMaxResults(5)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Double getScontrinoMedio() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT AVG(o.prezzoTotale) FROM Ordine o WHERE o.stato = 'consegnato'", Double.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Long getTotaleOrdini() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(o) FROM Ordine o", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Long getOrdiniAnnullati() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(o) FROM Ordine o WHERE o.stato = 'annullato'", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}