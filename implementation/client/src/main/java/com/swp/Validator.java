package com.swp;

import com.gumse.gui.Locale;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Controller.SingleDataCallback;

/**
 * Eine Applikation zur Verwaltung von Kursen und Teilnehmer:innen in den Kursen.
 *
 * @author Karsten Hölscher, modifications by Karteikarten AG
 */
public class Validator 
{
    public static <T> T checkNotNullOrBlank(final T object)
    {
        if (object == null) {
                throw new IllegalArgumentException("nonnull");
        }
        if (object instanceof String string && string.isBlank()) {

                throw new IllegalArgumentException("nonempty");
        }
        return object;
    }

}
