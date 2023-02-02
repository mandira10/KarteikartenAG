package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardToCategoryRepositoryTest {
    // Repositories die getestet werden
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();

    @Test
    public void cardToCategoryCrudTest() {
        List<Card> cards = exampleCards();
        List<Category> categories = exampleCategories();

        // CREATE
        CardToCategoryRepository.startTransaction();
        for (final Card card : cards) {
            for (final Category category : categories) {
                cardToCategoryRepository.save(new CardToCategory(card, category));
            }
        }
        CardToCategoryRepository.commitTransaction();

        // READ
        List<CardToCategory> allReadCards = new ArrayList<>();
        CardToCategoryRepository.startTransaction();
        for (final Card card : cards) {
            for (final Category category : categories) {
                //final CardToCategory readCard = cardToCategoryRepository.//getCardToCategoryByUUID(card.getUuid(),
                        //category.getUuid());
                //assertEquals(card, readCard.getCard());
                //assertEquals(category, readCard.getCategory());
                //allReadCards.add(readCard);
            }
        }
        CardToCategoryRepository.commitTransaction();
        assertEquals(cards.size() * categories.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));

        // UPDATE
        // TODO

        // DELETE
        // TODO

    }

    @Test
    public void getAllC2CForCategory() {
    }

    @Test
    public void getSpecific() {
    }

    @Test
    public void getAllC2CForCard() {
    }

    private List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        Collections.addAll(exampleCards,
        new AudioCard(),
        new AudioCard(),
        new ImageDescriptionCard(),
        new ImageDescriptionCard(),
        new ImageTestCard(),
        new ImageTestCard(),
        new TrueFalseCard(),
        new TrueFalseCard()
        );
        return exampleCards;
    }

    public List<Category> exampleCategories() {
        List<Category> exampleCategories = new ArrayList<>();
        Collections.addAll(exampleCategories,
                new Category("Kategorie 1"),
                new Category("Kategorie 2"),
                new Category("Kategorie 3")
        );
        return exampleCategories;
    }

}
