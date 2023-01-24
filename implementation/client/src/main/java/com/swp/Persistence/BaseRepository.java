package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Das BaseRepository stellt grundlegende Attribute und Funktionen für die spezifischen Repositories zur Verfügung.
 */
public abstract class BaseRepository<T> {
    // Objekt zur Synchronisation von mehreren potenziell parallelen Transaktionen
    public static final Object transaction = new Object();

    private static EntityManagerFactory emf = PersistenceManager.emFactory;

    @Getter(AccessLevel.PROTECTED)
    private static EntityManager entityManager;

    @Getter(AccessLevel.PROTECTED)
    private static CriteriaBuilder criteriaBuilder;

    private final Class<T> repoType;

    public BaseRepository(Class<T> repoType) {
        this.repoType = repoType;
    }

    public void refresh(T t){
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
        entityManager = emf.createEntityManager();
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
        }
    }

    /**
     * Funktion um zu prüfen, ob gerade eine Transaktion aktiv ist.
     * @return boolean, falls gerade eine aktive Transaktion vorliegt `true`, sonst `false`.
     */
    public static boolean isTransactionActive() {
        return entityManager != null && entityManager.isOpen() && entityManager.getTransaction().isActive();
    }

    // Allgemeine Funktionen für die Repositories (CRUD)
    /**
     * Funktion um eine Liste von Objekten aus dem entsprechenden Repository zu ziehen.
     * Die Objekte sind vom Typ des entsprechendem spezifischen Repository, welches BaseRepository<T> erweitert.
     * Dabei wird dabei nach einem `value` gefiltert.
     * @param value Object nach dem die Liste gefiltert werden soll
     * @param attribute
     */
    public List<T> findListBy(final Object value, final SingularAttribute<T, ?> attribute) {
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(repoType);
        final Root<T> queryRoot = criteriaQuery.from(repoType);

        criteriaQuery.where(
                criteriaBuilder.equal(
                        queryRoot.get(attribute),value)
        );

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Funktion um alle gespeicherten Objekte des Repository-Typs aus der Datenbank zu holen.
     * @return List<T> eine Liste mit allen persistierten Objekten aus der Datenbank.
     */
    public List<T> getAll() {
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(repoType);
        final Root<T> queryRoot = criteriaQuery.from(repoType);
        criteriaQuery.select(queryRoot);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Funktion um die Anzahl der persistierten Objekte des entsprechenden Repository-Typs zu bekommen.
     * @return int Anzahl der gespeicherten Objekte im Repository.
     */
    public int countAll(){
        return getAll().size();
    }

    /*
     * Funktion um einen bestimmten Abschnitt der persistierten Objekte aus der Datenbank zu holen
     * @param from     int: gibt an wie viele Zeilen des Ergebnisses übersprungen werden.
     * @param to       int: gibt an bis zu welcher Zeile die Ergebnisse geholt werden sollen.
     * @param callback DataCallback<T>: Callback-Instanz, über die die gefundenen Daten an die GUI gegeben werden.
     * @param order    CardOrder: in welcher Reihenfolge die Ergebnisse sortiert sein sollen.
    public List<T> getRange(final int from, final int to, final DataCallback<T> callback, Deck.CardOrder order) {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(repoType);
        final Root<T> queryRoot = criteriaQuery.from(repoType);
        criteriaQuery.select(queryRoot).orderBy(criteriaBuilder.asc('repoType_.columnName')).setFirstResult(from).setMaxResults(to-from);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
     //*/

    /**
     * Funktion um ein Objekt des Repository-Typs in dem entsprechenden Repository zu persistieren.
     * @param object das zu speichernde Objekt.
     */
    public T save(T object) {
        return entityManager.merge(object);
    }

    /**
     * Funktion um eine Liste von Objekten im Repository zu persistieren.
     * @param objects Liste der zu speichernden Objekte.
     */
    public List<T> save(List<T> objects) {
        var result = new ArrayList<T>();
        for (T object : objects) {
            result.add(save(object));
        }
        return result;
    }

    /**
     * Funktion um ein Objekt des Repository-Typs in dem entsprechenden Repository zu aktualisieren.
     * @param object das zu aktualisierende Objekt.
     */
    public T update(T object) {
        return entityManager.merge(object);
    }

    /**
     * Funktion um ein Objekt des Repository-Typs aus dem entsprechenden Repository zu entfernen.
     * @param object das zu löschende Objekt.
     */
    public void delete(T object) {
        entityManager.remove(object);
    }

    /**
     * Funktion um eine Liste von Objekten im Repository zu entfernen.
     * @param objects Liste der zu löschenden Objekte.
     */
    public void delete(List<T> objects) {
        for (T object : objects) {
            delete(object);
        }
    }
}
