package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Tag;

import java.util.List;

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

    public CardToTag findSpecificCardToTag(Card c, Tag t) {
        return getEntityManager()
                .createNamedQuery("CardToTag.findSpecificC2T", CardToTag.class)
                .setParameter("card", c)
                .setParameter("tag", t)
                .getSingleResult();
    }

    /**
     * Die Funktion `createCardToTag` erstellt eine neues `CardToTag`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param tag ein Tag, der der Karte zugeordnet werden soll
     */
    public CardToTag createCardToTag(Card card, Tag tag) {
        return getEntityManager().merge(new CardToTag(card, tag));
    }

    public List<CardToTag> getAllC2TForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allC2TByCard", CardToTag.class)
                .setParameter("card", card)
                .getResultList();
    }

    /**
     * Die Funktion `getCardToTags` liefer alle `CardToTag`-Objekte zurück.
     * Sie stellen die Verbindungen zwischen Karten und Tags dar.
     *
     * @return List<CardToTag> eine Menge mit allen `CardToTag`-Objekten.
     */
    public List<CardToTag> getCardToTags() {
        return getEntityManager()
                .createQuery("SELECT CardToTag FROM CardToTag", CardToTag.class)
                .getResultList();
    }

}
