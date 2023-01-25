package com.swp;

import com.gumse.gui.Locale;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Controller.SingleDataCallback;

/**
 * Eine Applikation zur Verwaltung von Kursen und Teilnehmer:innen in den Kursen.
 *
 * @author Karsten Hölscher, modifications by Karteikarten AG
 */
public class Validator {

    public static <T> T checkNotNullOrBlank(final T object, final String name, boolean internal) {
        if (object == null) {
            if(internal)
                throw new IllegalArgumentException(String.format("%s "+ Locale.getCurrentLocale().getString("nonnull"), name));
            else
            getDataCallback().onFailure(String.format("%s "+ Locale.getCurrentLocale().getString("nonnull"), name));
        }
        if (object instanceof String string && string.isBlank()) {//TODO
            if(internal)
                throw new IllegalArgumentException(String.format("%s "+ Locale.getCurrentLocale().getString("nonempty"), name));
            else
            getDataCallback().onFailure(String.format("%s "+ Locale.getCurrentLocale().getString("nonnull"), name));
        }
        return object;
    }


    private static SingleDataCallback<Object> getDataCallback() {
        SingleDataCallback<Object> callback = new SingleDataCallback<>() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {
                NotificationGUI.addNotification(msg, Notification.NotificationType.ERROR, 5);
            }
        };
        return callback;
    }

}
