package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class PersistenceManager {
    private static final String PU_NAME = "KarteikartenDB";
    private static final EntityManagerFactory emFactory;

    static {
        emFactory = Persistence.createEntityManagerFactory(PU_NAME);
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }
}
