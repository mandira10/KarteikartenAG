package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Tag;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Die Datenbank repository für CardToTag Objekte
 */
public class CardToTagRepository extends BaseRepository<CardToTag> {
    private CardToTagRepository() {
        super(CardToTag.class);
    }

    // Singleton
    private static CardToTagRepository cardToTagRepository = null;
    public static CardToTagRepository getInstance()
    {
        if(cardToTagRepository == null)
            cardToTagRepository = new CardToTagRepository();
        return cardToTagRepository;
    }

    /**
     * Holt für eine angegebene Karte und einen angegebenen Tag, die entsprechende
     * Karte-zu-Tag Verbindung aus der Datenbank.
     * Sollte diese Verbindung nicht existieren, wird eine Exception geworfen.
     *
     * @param c eine Karte
     * @param t ein Tag
     * @return eine Karte-zu-Tag-Verbindung
     * @throws NoResultException falls diese Verbindung nicht existiert.
     */
    public CardToTag findSpecificCardToTag(Card c, Tag t) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("CardToTag.findSpecificC2T", CardToTag.class)
                .setParameter("card", c)
                .setParameter("tag", t)
                .getSingleResult();
    }

    /**
     * Speicher für eine angegebene Karte und einem angegebenen Tag, die entsprechende
     * Karte-zu-Tag Verbindung in der Datenbank.
     *
     * @param card eine Karte, der dem Tag zugeordnet werden soll.
     * @param tag ein Tag, der der Karte zugeordnet werden soll.
     * @return CardToTag aus der Datenbank (attached Entity).
     */
    public CardToTag createCardToTag(Card card, Tag tag) {
        return getEntityManager().merge(new CardToTag(card, tag));
    }

    /**
     * Holt für eine angegebene Karte alle Karte-zu-Tag Verbindungen aus der Datenbank.
     * Sollte der Karte kein Tag zugeordnet sein, so wird eine leere Liste zurückgegeben.
     *
     * @param card eine Karte
     * @return eine Liste von Karte-zu-Tag Verbindungen.
     */
    public List<CardToTag> getAllC2TForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allC2TByCard", CardToTag.class)
                .setParameter("card", card)
                .getResultList();
    }
}
