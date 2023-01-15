package com.swp.DataModel.StudySystem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.*;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;

/**
 * Abstrakte Superklasse für das StudySystem von der die einzelnen
 * Systeme erben
 */

@Getter
@Setter
@Entity
@DiscriminatorColumn(name = "StudySystemType")
public abstract class StudySystem implements Serializable
{
    // TODO: verschiedene StudySystem-Typen persistieren
    //

    /**
     * Primärer Schlüssel für die persistierten `StudySysteme`
     */
    @Id
    //@GeneratedValue
    private final String id;

    /**
     * Zugehöriges Deck für das System
     */
    @OneToOne
    protected Deck deck;

    /**
     * Maximale Anzahl an Karteikarten-Boxen, die das Lernsystem haben darf.
     */
    protected int maxBoxes;

    /**
     * Konstruktor der Klasse StudySystem
     * @param deck: Deck des StudySystems
     * @param nboxes: Anzahl der Boxen für das StudySystem
     */
    public StudySystem(final Deck deck, final int nboxes)
    {
        this.deck = deck;
        this.maxBoxes = nboxes;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * No-Arg Konstruktor
     */
    public StudySystem() {
        this(null,0);
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards) {
        boxes.get(0).add(cards);
    }

    public Set<Card> getAllCardsInStudySystem() {
        Set<Card> cardsInStudyS = new HashSet<>();

        for(StudySystemBox box : boxes){
            cardsInStudyS.addAll(box.getBoxContent());
        }
        return cardsInStudyS;
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param answer: Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(boolean answer) { }

    //TO IMPLEMENT
    public void giveRating(int rating) { };

    //TO IMPLEMENT
    public void giveTime(float seconds) { };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest() {}

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public int getResult() { return 0; }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    Iterator<Card> it = null;
    public Card getNextCard(int index)
    {
        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        if(it == null)
            it = DeckController.getCardsInDeck(deck).iterator();
        if(it.hasNext())
            return (Card)it.next();
            
        it = null;
        return null;
        /////////////////////////////////////////////////////////////////
    }

    //NEEDS TO BE IMPLEMENTED
    public float getProgress()
    {
        return (float)Math.random();  //Should return percentage as: 0.0 ... 1.0
    }
}