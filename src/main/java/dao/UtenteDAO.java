package dao;

import model.Utente;
import util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.List;

public class UtenteDAO {

    public Utente getUtenteByEmailPassword(String email, String password) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM Utente u WHERE u.email = :email AND u.password = :password", Utente.class)
                     .setParameter("email", email)
                     .setParameter("password", password)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public void salvaUtente(Utente u) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(u);
            em.getTransaction().commit();
        } catch(Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Utente> getStaffList() {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM Utente u WHERE u.ruolo = 'personale' ORDER BY u.nomeCompleto ASC", Utente.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Utente getUtenteById(Long id) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Utente.class, id);
        } finally {
            em.close();
        }
    }

    public void aggiornaStaff(Long id, String nome, String email, String telefono, String password) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Utente u = em.find(Utente.class, id);
            if (u != null) {
                u.setNomeCompleto(nome);
                u.setEmail(email);
                u.setTelefono(telefono);
                if (password != null && !password.trim().isEmpty()) {
                    u.setPassword(password);
                }
            }
            em.getTransaction().commit();
        } catch(Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminaStaff(Long idDaEliminare, Long idLoggato) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Utente u = em.find(Utente.class, idDaEliminare);
            if (u != null && !u.getId().equals(idLoggato)) {
                em.remove(u);
            }
            em.getTransaction().commit();
        } catch(Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void aggiornaProfilo(Long id, String nomeCompleto, String telefono, String indirizzo) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Utente utente = em.find(Utente.class, id);
            if (utente != null) {
                utente.setNomeCompleto(nomeCompleto);
                utente.setTelefono(telefono);
                utente.setIndirizzo(indirizzo);
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