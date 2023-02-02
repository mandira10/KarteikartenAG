package com.swp.DataModel;


import com.gumse.gui.Locale;
import com.swp.TestDataClass;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Settings.Setting;
import com.swp.GUI.Extras.Notification.NotificationType;
import com.swp.GUI.PageManager.PAGES;
import com.swp.GUI.KarteikartenAGGUI;
import com.swp.GUI.PageManager;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Persistence.PersistenceManager;

public class User 
{
    private static User pLoggedInUser = null;
    private String sUsername, sPassword;

    public User(String username, String password)
    {
        sUsername = username;
        sPassword = password;
    }

    public static void loginUser(User user)
    {
        if(user.getUsername().equals("null"))
            return;
        
        pLoggedInUser = user;
        
        String host = Settings.getInstance().getSetting(Setting.SERVER_ADDRESS);
        String port = Settings.getInstance().getSetting(Setting.SERVER_PORT);
        PersistenceManager.changeH2Server(host, port, user.getUsername(), user.getPassword(), new SingleDataCallback<Boolean>() {
            @Override protected void onFailure(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.CONNECTION, 7);
                logout();
            }
            @Override protected void onSuccess(Boolean data) 
            {
                NotificationGUI.addNotification(Locale.getCurrentLocale().getString("loginmessage"), NotificationType.INFO, 7);
                Settings.getInstance().setSetting(Setting.USER_NAME, user.getUsername());
                Settings.getInstance().setSetting(Setting.USER_PASSWD, user.getPassword());
                KarteikartenAGGUI.getInstance().getSidebar().refresh();
                PageManager.viewPage(PAGES.DECK_OVERVIEW);
            }
        });
    }

    public static void demoUser()
    {
        pLoggedInUser = new User("Demo User", "none");
        PersistenceManager.demoServer(new SingleDataCallback<Boolean>() {
            @Override protected void onFailure(String msg) {
                NotificationGUI.addNotification(msg, NotificationType.CONNECTION, 7);
                logout();
            }
            @Override protected void onSuccess(Boolean data) 
            {
                NotificationGUI.addNotification(Locale.getCurrentLocale().getString("loginmessage"), NotificationType.INFO, 7);
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