package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.swp.Controller.SingleDataCallback;

public class PersistenceManager 
{
    private static String puName;
    private static EntityManagerFactory emFactory = null;

    public static void init() { init("KarteikartenDB"); }
    public static void init(String puname)
    {
        puName = puname;
        emFactory = Persistence.createEntityManagerFactory(puName);
    }

    public static EntityManager getEntityManager()
    {
        return emFactory.createEntityManager();
    }

    /**
     * Ändert die Optionen aus der `persistence.xml` zur Laufzeit mit übergebenen Werten.
     * Damit kann zum Beispiel ein vom Nutzer angegebener Datenbankserver benutzt werden.
     * @param host die Serveradresse eines H2 Datenbankservers (z.B. 127.0.0.1).
     * @param port der port eines H2 Datenbankservers (z.B. 8082).
     * @param user der Nutzer für die Anmeldung am Datenbankserver.
     * @param password das Password zur Anmeldung am Datenbankserver.
     */
    public static void changeH2Server(String host, String port, String user, String password, SingleDataCallback<Boolean> callback) 
    {
        if(host == null || host.isEmpty())
        {
            runLocalH2Server(user, password, callback);
            return;
        }
        Output.info("Logging in as " + user + " [" + password + "] onto " + host + ":" + port);
        emFactory.close();

        Map<String,String> persistenceOptions = new HashMap<String, String>();
        persistenceOptions.put("jakarta.persistence.jdbc.url", "jdbc:h2:tcp://" + host + ":" + port);
        persistenceOptions.put("jakarta.persistence.jdbc.user", user);
        persistenceOptions.put("jakarta.persistence.jdbc.password", password);
        persistenceOptions.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        persistenceOptions.put("org.hibernate.dialect", "h2");

        try { emFactory = Persistence.createEntityManagerFactory(puName, persistenceOptions); }
        catch(Exception e)
        {
            callback.callFailure(Locale.getCurrentLocale().getString("serverconnectionfailed") + ": " + host + ":" + port);
            return;
        }
        callback.callSuccess(true);
    }

    /**
     * Ändert die Optionen aus der `persistence.xml` zur Laufzeit mit übergebenen Werten.
     * Damit kann zum Beispiel ein vom Nutzer angegebener Datenbankserver benutzt werden.
     * @param user der Nutzer für die Anmeldung am Datenbankserver.
     * @param password das Password zur Anmeldung am Datenbankserver.
     */
    public static void runLocalH2Server(String user, String password, SingleDataCallback<Boolean> callback) 
    {
        Output.info("Logging in as " + user + " [" + password + "] onto localhost");
        emFactory.close();

        Map<String,String> persistenceOptions = new HashMap<String, String>();
        persistenceOptions.put("jakarta.persistence.jdbc.url", "jdbc:h2:./db/karteikarten-ag.h2;AUTO_SERVER=TRUE");
        persistenceOptions.put("jakarta.persistence.jdbc.user", user);
        persistenceOptions.put("jakarta.persistence.jdbc.password", password);
        persistenceOptions.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        persistenceOptions.put("org.hibernate.dialect", "h2");

        try { emFactory = Persistence.createEntityManagerFactory(puName, persistenceOptions); }
        catch(Exception e)
        {
            callback.callFailure(Locale.getCurrentLocale().getString("serverstartfailed"));
            return;
        }
        callback.callSuccess(true);
    }

    public static void demoServer(SingleDataCallback<Boolean> callback) 
    {
        emFactory.close();

        try { emFactory = Persistence.createEntityManagerFactory("demo"); }
        catch(Exception e)
        {
            callback.callFailure(Locale.getCurrentLocale().getString("serverconnectionfailed"));
            return;
        }
        callback.callSuccess(true);
    }

    public static void close()
    {
        emFactory.close();
    }

}
