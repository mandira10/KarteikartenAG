package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CrudTest {
    // Repositories die getestet werden
    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private CategoryRepository categoryRepository;
    private CardToDeckRepository cardToDeckRepository;
    private CardToCategoryRepository cardToCategoryRepository;

    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    private List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        //TODO Karten mit Inhalt füllen
        Collections.addAll(exampleCards,
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new ImageDescriptionCard("Sind das komische Fragen?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", "/path/to/image.png", false),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new MultipleChoiceCard(),
                new MultipleChoiceCard(),
                new MultipleChoiceCard(),
                new MultipleChoiceCard(),
                new MultipleChoiceCard(),
                new TextCard(),
                new TextCard(),
                new TextCard(),
                new TextCard(),
                new TextCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard()
        );

        return exampleCards;
    }

    private List<Deck> exampleDecks() {
        //TODO Test-Decks erstellen
        return null;
    }

    private List<Category> exampleCategories() {
        //TODO Test-Kategorien erstellen
        return null;
    }


    @BeforeEach
    public void setup() {
        cardRepository = CardRepository.getInstance();
        deckRepository = DeckRepository.getInstance();
        categoryRepository = CategoryRepository.getInstance();
        cardToDeckRepository = CardToDeckRepository.getInstance();
        cardToCategoryRepository = CardToCategoryRepository.getInstance();
    }

    @Test
    public void cardCrudTest() {
        List<Card> cards = exampleCards();

        // CREATE
        for (final Card card : cards) {
            cardRepository.save(card);
        }

        // READ
        for (final Card card : cards) {
            final Card readCard = cardRepository.getCardByUUID(card.getUuid());
            assertEquals(card, readCard);
        }

        // UPDATE
        //TODO

        // DELETE
        //TODO

    }

    @Test
    public void deckCrudTest() {

    }

    @Test
    public void categoryCrudTest() {

    }

    @Test
    public void cardToDeckCrudTest() {

    }

    @Test
    public void cardToCategoryCrudTest() {

    }
}
