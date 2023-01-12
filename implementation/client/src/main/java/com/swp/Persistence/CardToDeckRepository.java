package com.swp.Persistence;

import com.swp.DataModel.CardToDeck;

public class CardToDeckRepository extends BaseRepository<CardToDeck> {
    private CardToDeckRepository() {
        super(CardToDeck.class);
    }

    // Singleton
    private static CardToDeckRepository cardToDeckRepository = null;
    public static CardToDeckRepository getInstance()
    {
        if(cardToDeckRepository == null)
            cardToDeckRepository = new CardToDeckRepository();
        return cardToDeckRepository;
    }
}
