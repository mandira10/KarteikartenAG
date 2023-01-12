package com.swp.Persistence;

import com.swp.DataModel.CardToTag;

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
}
