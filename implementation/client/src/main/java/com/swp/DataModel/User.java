package com.swp.DataModel;


import com.gumse.gui.Locale;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Settings.Setting;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;
import com.swp.Persistence.PersistenceManager;
import com.swp.TestDataClass;

/**
 * Die Klasse welche Benutzer an- und abmeldet
 * @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */
public class User
{
    private static User pLoggedInUser = null;
    private final String sUsername;
    private final String sPassword;

    /**
     * Der Hauptkonstruktor der Klasse Sidebar
     *
     * @param username Der benutzername
     * @param password Das Passwort
     */
    public User(String username, String password)
    {
        sUsername = username;
        sPassword = password;
    }

    /**
     * Trägt einen Benutzer als angemeldet ein
     *
     * @param user Der benutzer
     */
    public static void loginUser(User user)
    {
        if(user.getUsername().equals("null"))
            return;
        
        pLoggedInUser = user;
        
        String host = Settings.getInstance().getSetting(Setting.SERVER_ADDRESS);
        String port = Settings.getInstance().getSetting(Setting.SERVER_PORT);
        PersistenceManager.changeH2Server(host, port, user.getUsername(), user.getPassword(), new SingleDataCallback<>() {
            @Override protected void onFailure(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.CONNECTION, 7);
                logout();
            }
            @Override protected void onSuccess(Boolean data) 
            {
                NotificationGUI.addNotification(Locale.getCurrentLocale().getString("loginmessage"), NotificationType.CONNECTION, 7);
                Settings.getInstance().setSetting(Setting.USER_NAME, user.getUsername());
                Settings.getInstance().setSetting(Setting.USER_PASSWD, user.getPassword());
                KarteikartenAGGUI.getInstance().getSidebar().refresh();
                PageManager.viewPage(PAGES.DECK_OVERVIEW);
            }
        });
    }

    /**
     * Erstellt einen Demobenutzer
     */
    public static void demoUser()
    {
        pLoggedInUser = new User("Demo User", "none");
        PersistenceManager.demoServer(new SingleDataCallback<>() {
            @Override protected void onFailure(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.CONNECTION, 7);
                logout();
            }
            @Override protected void onSuccess(Boolean data) 
            {
                NotificationGUI.addNotification(Locale.getCurrentLocale().getString("loginmessage"), NotificationType.CONNECTION, 7);
                KarteikartenAGGUI.getInstance().getSidebar().refresh();
                PageManager.viewPage(PAGES.DECK_OVERVIEW);
            }
        });


        ControllerThreadPool.getInstance().synchronizedTasks(true);
        TestDataClass.importTestData();
        ControllerThreadPool.getInstance().synchronizedTasks(false);
    }


    public String getUsername()
    {
        return this.sUsername;
    }

    public String getPassword()
    {
        return this.sPassword;
    }

    /**
     * Meldet den aktuell angemeldeten User ab
     */
    public static void logout()
    {
        pLoggedInUser = null;
    }

    public static User getLoggedInUser()
    {
        return pLoggedInUser;
    }

    public static boolean isLoggedIn()
    {
        return pLoggedInUser != null;
    }
}