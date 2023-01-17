package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
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
    public void createCardToTag(Card card, Tag tag) {
        getEntityManager().persist(new CardToTag(card, tag));
    }

    public List<CardToTag> getAllC2TForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allC2TByCard", CardToTag.class)
                .setParameter("card", card)
                .getResultList();
    }

}
