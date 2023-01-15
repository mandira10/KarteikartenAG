package com.swp.Persistence;

import com.swp.DataModel.CardToCategory;

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
}
