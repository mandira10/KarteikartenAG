package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Die Datenbank repository für CardToCategory Objekte
 * @author Ole-Nikas Mahlstädt
 */
public class CardToCategoryRepository extends BaseRepository<CardToCategory> {
    private CardToCategoryRepository() {
        super(CardToCategory.class);
    }

    // Singleton
    private static CardToCategoryRepository cardToCategoryRepository = null;
    public static CardToCategoryRepository getInstance()
    {
        if(cardToCategoryRepository == null)
            cardToCategoryRepository = new CardToCategoryRepository();
        return cardToCategoryRepository;
    }

    /**
     * Holt für eine angegebene Kategorie eine Liste von Karten-zu-Kategorie Verbindungen aus der Datenbank.
     * Sollte der Kategorie keine Karte zugeordnet sein, so wird eine leere Liste zurückgegeben.
     *
     * @param category eine Kategorie
     * @return eine Liste von Karte-zu-Kategorie Verbindungen.
     */
    public static List<CardToCategory> getAllC2CForCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allC2CByCategory", CardToCategory.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * Holt für eine angegebene Karte und eine angegebene Kategorie, die entsprechende Karte-zu-Kategorie Verbindung
     * aus der Datenbank. Sollte diese Verbindung nicht existieren, wird eine Exception geworfen.
     *
     * @param card eine Karte
     * @param cat  eine Kategorie
     * @return     eine Karte-zu-Kategorie Verbidung
     * @throws     NoResultException falls diese Verbindung nicht existiert
     */
    public CardToCategory getSpecific(Card card, Category cat) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("CardToCategory.findSpecificC2C", CardToCategory.class)
                .setParameter("card", card)
                .setParameter("category", cat)
                .getSingleResult();
    }

    /**
     * Holt für eine Karte eine Liste von allen Karten-zu-Kategorie Verbindungen aus der Datenbank.
     * Sollte die Karte keiner Kategorie zugeordnet sein, so wird eine leere Liste zurückgegeben.
     *
     * @param card eine Karte
     * @return eine Liste von Karten-zu-Kategorie Verbindungen.
     */
    public List<CardToCategory> getAllC2CForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allC2CByCard", CardToCategory.class)
                .setParameter("card", card)
                .getResultList();
    }
}
