package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CardRepositoryTest {
    // Repositories die getestet werden
    private final CardRepository cardRepository = CardRepository.getInstance();

    @Test
    public void cardCrudTest() {
        List<Card> cards = exampleCards();

        // CREATE
        CardRepository.startTransaction();
        for (final Card card : cards) {
            cardRepository.save(card);
        }
        CardRepository.commitTransaction();

        // READ
        List<Card> allReadCards = new ArrayList<>();
        CardRepository.getEntityManager().getTransaction().begin();
        for (final Card card : cards) {
            final Card readCard = cardRepository.getCardByUUID(card.getUuid());
            assertEquals(card, readCard);
            allReadCards.add(readCard);
        }
        CardRepository.getEntityManager().getTransaction().commit();
        assertEquals(cards.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));

        // UPDATE
        //TODO

        // DELETE
        //TODO

    }




    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    public List<Card> exampleCards() {
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
                new MultipleChoiceCard("Frage 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1", false),
                new MultipleChoiceCard("Frage 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2", true),
                new MultipleChoiceCard("Frage 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3", false),
                new MultipleChoiceCard("Frage 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4", true),
                new MultipleChoiceCard("Frage 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5", false),
                new TextCard("Frage", "Antwort", "Titel", false),
                new TextCard("Frage 2", "Antwort auf die Frage", "Titel XY", false),
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
}
