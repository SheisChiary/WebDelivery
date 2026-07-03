package dao;

import model.Prodotto;
import model.Caratteristica;
import util.JpaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ProdottoDAO {

    public List<Prodotto> getAllProdotti() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prodotto p", Prodotto.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Prodotto> getBevande(int limit) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prodotto p WHERE p.categoria = 'Bevande'", Prodotto.class)
                     .setMaxResults(limit)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Prodotto getProdottoById(Long id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Prodotto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Caratteristica> getCaratteristicheByProdotto(Long pid) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Caratteristica c LEFT JOIN FETCH c.gruppo WHERE c.prodotto.id = :pid", Caratteristica.class)
                     .setParameter("pid", pid)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Caratteristica> getDefaultCaratteristiche(Long pid) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Caratteristica c WHERE c.prodotto.id = :pid AND c.isDefault = true", Caratteristica.class)
                     .setParameter("pid", pid)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    public Caratteristica getCaratteristicaById(Long cid) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Caratteristica.class, cid);
        } finally {
            em.close();
        }
    }
}