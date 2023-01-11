package com.swp.Persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AccessLevel;
import lombok.Getter;

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
}
