
package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystemBox;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardToBoxRepositoryTest {

    // Repositories die getestet werden
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    @Test
    public void cardToBoxCrudTest() {
        List<Card> cards = exampleCards();
        List<StudySystemBox> boxes = exampleBoxes();

        // CREATE
        CardToBoxRepository.startTransaction();
        for (final Card card : cards) {
            for (final StudySystemBox box : boxes) {
                cardToBoxRepository.save(new BoxToCard(card, box));
            }
        }
        CardToBoxRepository.commitTransaction();

        // READ
        List<BoxToCard> allReadCards = new ArrayList<>();
        CardToBoxRepository.startTransaction();
        for (final Card card : cards) {
            for (final StudySystemBox box : boxes) {
                //final BoxToCard readCard = cardToBoxRepository.//getCardToBoxByUUID(card.getUuid(),
                        //box.getUuid());
                //assertEquals(card, readCard.getCard());
                //assertEquals(box, readCard.getStudySystemBox());
                //allReadCards.add(readCard);
            }
        }
        CardToBoxRepository.commitTransaction();
        assertEquals(cards.size() * boxes.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));

        // UPDATE
        //TODO

        // DELETE
        //TODO

    }

    

    @Test
    public void getAllBoxToCardsForStudySystem() {

    }

    @Test
    public void getAllB2CForCard() {

    }

    @Test
    public void getAllB2CForStudySystem() {

    }

    @Test
    public void getSpecific() {

    }

    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    public List<Card> exampleCards() {
    List<Card> exampleCards = new ArrayList<>();
    Texture loadTex = new Texture();
    loadTex.loadFile("/path/to/image.png", getClass());
    //TODO Karten mit Inhalt füllen
    Collections.addAll(exampleCards,
            new AudioCard(),
            new AudioCard(),
            new AudioCard(),
            new AudioCard(),
            new AudioCard(),    
            new ImageDescriptionCard("Sind das komische Fragen?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", new byte[loadTex.getData().remaining()]),
            new ImageDescriptionCard(),
            new ImageDescriptionCard(),
            new ImageDescriptionCard(),
            new ImageDescriptionCard(),
            new ImageTestCard(),
            new ImageTestCard(),
            new ImageTestCard(),
            new ImageTestCard(),
            new ImageTestCard(),
            new MultipleChoiceCard("Frage 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1"),
            new MultipleChoiceCard("Frage 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2"),
            new MultipleChoiceCard("Frage 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3"),
            new MultipleChoiceCard("Frage 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4"),
            new MultipleChoiceCard("Frage 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5"),
            new TextCard("Frage", "Antwort", "Titel"),
            new TextCard("Frage 2", "Antwort auf die Frage", "Titel XY"),
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

    private List<StudySystemBox> exampleBoxes() {
        List<StudySystemBox> exampleBoxes = new ArrayList<>();
        Collections.addAll(exampleBoxes,
                new StudySystemBox()
        );
        return exampleBoxes;
    }

    
}

