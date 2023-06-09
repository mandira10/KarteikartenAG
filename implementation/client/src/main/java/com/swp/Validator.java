package com.swp;

/**
 * Eine Applikation zur Verwaltung von Kursen und Teilnehmer:innen in den Kursen.
 *
 * @author Karsten Hölscher, modifications by Nadja Cordes
 */
public class Validator 
{
    /**
     * Überprüft ob, ein gegebenes Objekt null oder im Fall eines Strings leer ist
     *
     * @param object Das zu prüfende Objekt
     * @param <T>    Der Typ des Objekts
     * @return Das Objekt, falls dies nicht null oder leer ist
     */
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
