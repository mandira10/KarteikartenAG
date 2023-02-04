package com.swp.Persistence;

import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.Language.German;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardToCategoryRepositoryTest {
    // Repositories die getestet werden
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();
    private final List<Card> cards = exampleCards();
    private final List<Category> categories = exampleCategories();

    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }

    @BeforeEach
    public void beforeEach() {
        BaseRepository.startTransaction();
        CardRepository.getInstance().delete(CardRepository.getInstance().getAll());
        CategoryRepository.getInstance().delete(CategoryRepository.getInstance().getAll());
        BaseRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToCategoryRepository.delete(cardToCategoryRepository.getAll());

        for (Card card : cards) {
            for (Category category : categories) {
                cardToCategoryRepository.save(new CardToCategory(card, category));
            }
        }
        CardToCategoryRepository.commitTransaction();
    }

    @Test
    public void cardToCategoryCrudTest() {
        // DELETE (all)
        CardToCategoryRepository.startTransaction();
        assertDoesNotThrow(() -> cardToCategoryRepository.delete(cardToCategoryRepository.getAll()));
        assertEquals(0, cardToCategoryRepository.countAll());
        CardToCategoryRepository.commitTransaction();

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
                final CardToCategory readCardToCategory = cardToCategoryRepository.getSpecific(card, category);
                assertEquals(card, readCardToCategory.getCard());
                assertEquals(category, readCardToCategory.getCategory());
                allReadCards.add(readCardToCategory);
            }
        }
        assertEquals(cards.size() * categories.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.stream().map(CardToCategory::getCard).toList().containsAll(cards));
        CardToCategoryRepository.commitTransaction();

        // UPDATE
        // alle Attribute sind final, man kann nur löschen oder neue Verbindungen anlegen.

        // DELETE (specific)
        CardToCategoryRepository.startTransaction();
        CardToCategory toDelete = cardToCategoryRepository.getSpecific(cards.get(0), categories.get(0));
        assertDoesNotThrow(() -> cardToCategoryRepository.delete(toDelete));
        assertThrows(NoResultException.class, () -> cardToCategoryRepository.getSpecific(cards.get(0), categories.get(0)));
        CardToCategoryRepository.commitTransaction();
    }

    @Test
    public void getAllC2CForCategory() {
        CardRepository.startTransaction();
        assertEquals(CardRepository.getInstance().getCardsByCategory(categories.get(0)).size(), cards.size());
        CardRepository.commitTransaction();
    }

    @Test
    public void getSpecific() {
        CardToCategoryRepository.startTransaction();
        assertNotNull(cardToCategoryRepository.getSpecific(cards.get(2), categories.get(1)));
        CardToCategoryRepository.commitTransaction();
    }

    @Test
    public void getAllC2CForCard() {
        CategoryRepository.startTransaction();
        assertEquals(CategoryRepository.getInstance().getCategoriesToCard(cards.get(0)).size(), categories.size());
        CategoryRepository.commitTransaction();
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
