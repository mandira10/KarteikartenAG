package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import java.util.List;

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

    public static List<CardToCategory> getAllC2CForCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allC2CByCategory", CardToCategory.class)
                .setParameter("category", category)
                .getResultList();
    }

    public static CardToCategory getSpecific(Card card, Category cat) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.findSpecificC2C", CardToCategory.class)
                .setParameter("card", card)
                .setParameter("category", cat)
                .getSingleResult();
    }

    //TODO: getAllC2CForCard
}
