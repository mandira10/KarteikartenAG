package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class PersistenceManager {
    private static final String PU_NAME = "KarteikartenDB";
    protected static EntityManagerFactory emFactory;

    static {
        emFactory = Persistence.createEntityManagerFactory(PU_NAME);
    }

    public EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }

    /**
     * Ändert die Optionen aus der `persistence.xml` zur Laufzeit mit übergebenen Werten.
     * Damit kann zum Beispiel ein vom Nutzer angegebener Datenbankserver benutzt werden.
     * @param server die Serveradresse eines H2 Datenbankservers (z.B. 127.0.0.1:8082).
     * @param user der Nutzer für die Anmeldung am Datenbankserver.
     * @param password das Password zur Anmeldung am Datenbankserver.
     */
    public static void changeH2Server(String server, String user, String password) {
        emFactory = null;

        Map<String,String> persistenceOptions = new HashMap<String, String>();

        persistenceOptions.put("jakarta.persistence.jdbc.url", "jdbc:h2:tcp://" + server);
        persistenceOptions.put("jakarta.persistence.jdbc.user", user);
        persistenceOptions.put("jakarta.persistence.jdbc.password", password);
        persistenceOptions.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        persistenceOptions.put("org.hibernate.dialect", "h2");

        emFactory = Persistence.createEntityManagerFactory(PU_NAME, persistenceOptions);
    }
}
