package utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    
    
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("WebDeliveryPU");

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}