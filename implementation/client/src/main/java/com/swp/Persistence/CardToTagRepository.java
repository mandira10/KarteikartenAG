package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Tag;

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

    public static CardToTag findSpecificCardToTag(Card c, Tag t) {
        return getEntityManager()
                .createNamedQuery("CardToTag.findSpecificC2T", CardToTag.class)
                .setParameter("card", c)
                .setParameter("tag", t)
                .getSingleResult();
    }
}
