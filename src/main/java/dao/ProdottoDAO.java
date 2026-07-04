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
            return em.createQuery("SELECT DISTINCT p FROM Prodotto p LEFT JOIN FETCH p.caratteristiche", Prodotto.class).getResultList();
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
    
    public void eliminaProdotto(Long idProdotto) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Prodotto p = em.find(Prodotto.class, idProdotto);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void aggiornaProdotto(Long id, String nome, double prezzo, String descrizione, String categoria, String immagine, int tempoPreparazione, String badge) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Prodotto p = em.find(Prodotto.class, id);
            if (p != null) {
                p.setNome(nome);
                p.setPrezzo(prezzo);
                p.setDescrizione(descrizione);
                p.setCategoria(categoria);
                p.setImmagine(immagine);
                p.setTempoPreparazione(tempoPreparazione);
                p.setBadge(badge);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void salva(Prodotto p) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void aggiungiCaratteristica(Long idProdotto, String nome, double differenzaPrezzo, boolean isDefault, Long idGruppo) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Prodotto p = em.find(Prodotto.class, idProdotto);
            Caratteristica c = new Caratteristica();
            c.setNome(nome);
            c.setDifferenzaPrezzo(differenzaPrezzo);
            c.setIsDefault(isDefault);
            c.setProdotto(p);

            if (idGruppo != null) {
                model.GruppoEsclusione g = em.find(model.GruppoEsclusione.class, idGruppo);
                c.setGruppo(g);
            }
            em.persist(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public List<model.Prodotto> getProdottiFiltrati(String search, String categoria) {
        jakarta.persistence.EntityManager em = util.JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            String jpql = "SELECT p FROM Prodotto p WHERE 1=1";
            if (categoria != null && !categoria.trim().isEmpty() && !categoria.equals("Tutti")) {
                jpql += " AND p.categoria = :categoria";
            }
            if (search != null && !search.trim().isEmpty()) {
                jpql += " AND (LOWER(p.nome) LIKE :search OR LOWER(p.descrizione) LIKE :search)";
            }
            var query = em.createQuery(jpql, model.Prodotto.class);
            if (categoria != null && !categoria.trim().isEmpty() && !categoria.equals("Tutti")) {
                query.setParameter("categoria", categoria);
            }
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void eliminaCaratteristica(Long idCaratt) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Caratteristica c = em.find(Caratteristica.class, idCaratt);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}