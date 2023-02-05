package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Das BaseRepository stellt grundlegende Attribute und Funktionen für die spezifischen Repositories zur Verfügung.
 * @param <T> Der Datentyp der Repository
 * @author Ole-Nikas Mahlstädt
 */
public abstract class BaseRepository<T>
{
    /**
     * Objekt zur Synchronisation von mehreren potenziell parallelen Transaktionen
     */
    public static final Object transaction = new Object();

    @Getter(AccessLevel.PROTECTED)
    private static EntityManager entityManager;

    @Getter(AccessLevel.PROTECTED)
    private static CriteriaBuilder criteriaBuilder;

    private final Class<T> repoType;

    /**
     * Der Basiskonstruktor von allen Repositories
     * @param repoType Der Klassentyp der Repository
     */
    public BaseRepository(Class<T> repoType) {
        this.repoType = repoType;
    }

    /**
     * Funktion um ein Object mit den Daten aus der abzugleichen.
     * Nach dem Aufruf ist `t` mit denselben Daten gefüllt, wie sie im Repository vorliegen.
     *
     * @param t das zu aktualisierende Object vom Typ T (Repository-Typ).
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public void refresh(T t) {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        entityManager.refresh(t);
    }

    /**
     * Funktion um eine Transaktion zu beginnen.
     * Dabei wird bereits auf bestimmte Fehler geprüft.
     */
    public static void startTransaction() {
        if (entityManager != null && entityManager.isOpen()) {
            throw new IllegalStateException("Eine Transaktion ist noch aktiv.");
        }
        entityManager = PersistenceManager.getEntityManager();
        entityManager.getTransaction().begin();
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    /**
     * Funktion um eine Transaktion zu abzusetzen.
     * Dabei wird anschließend auch der EntityManager geschlossen.
     */
    public static void commitTransaction() {
        if (entityManager == null || !entityManager.isOpen()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        try {
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
            entityManager = null;
            criteriaBuilder = null;
        }
    }

    /**
     * Funktion um eine offene Transaktion zu verwerfen/zurückzusetzen.
     * Dabei wird der verwendete EntityManager geschlossen.
     */
    public static void rollbackTransaction() {
        if (entityManager == null || !entityManager.isOpen()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        try {
            entityManager.getTransaction().rollback();
        } finally {
            entityManager.close();
            entityManager = null;
            criteriaBuilder = null;
        }
    }

    /**
     * Funktion um zu prüfen, ob gerade eine Transaktion aktiv ist.
     *
     * @return boolean, falls gerade eine aktive Transaktion vorliegt `true`, sonst `false`.
     */
    public static boolean isTransactionActive() {
        return entityManager != null && entityManager.isOpen() && entityManager.getTransaction().isActive();
    }

    // Allgemeine Funktionen für die Repositories (CRUD)

    /**
     * Funktion um alle gespeicherten Objekte des Repository-Typs aus der Datenbank zu holen.
     *
     * @return List<T> eine Liste mit allen persistierten Objekten aus der Datenbank.
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public List<T> getAll() throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(repoType);
        final Root<T> queryRoot = criteriaQuery.from(repoType);
        criteriaQuery.select(queryRoot);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Funktion um die Anzahl der persistierten Objekte des entsprechenden Repository-Typs zu bekommen.
     *
     * @return int Anzahl der gespeicherten Objekte im Repository.
     */
    public int countAll() {
        return getAll().size();
    }

    /**
     * Funktion um ein Objekt des Repository-Typs in dem entsprechenden Repository zu persistieren.
     *
     * @param object das zu speichernde Objekt.
     * @throws IllegalStateException, wenn bei Aufruf keine Transaktion offen ist.
     * @return die attached-Entity des gespeicherten Objekts.
     */
    public T save(T object) throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        return entityManager.merge(object);
    }

    /**
     * Funktion um eine Liste von Objekten im Repository zu persistieren.
     *
     * @param objects Liste der zu speichernden Objekte.
     * @return List<T> eine Liste der attached Objects die gerade gespeichert wurden.
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public List<T> save(List<T> objects) throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        var result = new ArrayList<T>();
        for (T object : objects) {
            result.add(save(object));
        }
        return result;
    }

    /**
     * Funktion um ein Objekt des Repository-Typs in dem entsprechenden Repository zu aktualisieren.
     *
     * @param object das zu aktualisierende Objekt.
     * @return T das attached Object vom Repository-Typ das gerade aktualisiert wurde.
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public T update(T object) throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        return entityManager.merge(object);
    }

    /**
     * Funktion um ein Objekt des Repository-Typs aus dem entsprechenden Repository zu entfernen.
     *
     * @param object das zu löschende Objekt.
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public void delete(T object) throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
    }

    /**
     * Funktion um eine Liste von Objekten im Repository zu entfernen.
     *
     * @param objects Liste der zu löschenden Objekte.
     * @throws IllegalStateException wenn bei Aufruf keine Transaktion offen ist.
     */
    public void delete(List<T> objects) throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Es gibt keine aktive Transaktionen.");
        }
        for (T object : objects) {
            delete(object);
        }
    }
}
