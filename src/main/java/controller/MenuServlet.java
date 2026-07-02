package controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Prodotto;
import utils.JpaUtil;


@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        
EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();        
        try {
          
            List<Prodotto> listaProdotti = em.createQuery("SELECT p FROM Prodotto p", Prodotto.class).getResultList();
            
           
            request.setAttribute("prodotti", listaProdotti);
            
           
            System.out.println("Trovati " + listaProdotti.size() + " prodotti nel database!");
            
           
            response.getWriter().println("Guarda la console di NetBeans per vedere i prodotti caricati da JPA!");
            
        } finally {
       
            em.close();
        }
    }
}