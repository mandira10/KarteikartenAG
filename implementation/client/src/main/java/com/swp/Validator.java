package com.swp;

/**
 * Eine Applikation zur Verwaltung von Kursen und Teilnehmer:innen in den Kursen.
 *
 * @author Karsten Hölscher
 */
public class Validator {

    public static <T> T checkNotNullOrBlank(final T object,final String name){
        if (object ==null){
            throw new IllegalArgumentException(String.format("%s must not be null!", name));

        }
        if (object instanceof String string &&  string.isBlank()) {
            throw new IllegalArgumentException(String.format("%s must  not be empty or blank!", name));
        }
        return object;

    }
}
