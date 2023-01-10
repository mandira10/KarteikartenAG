package com.swp.DataModel.StudySystem;

import com.swp.DataModel.CardTypes.MultipleChoiceCard;
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
public abstract class StudySystem implements Serializable
{
    /**
     *TODO: wofür?
     */
   // private String[] asProfiles; //TODO

    /**
     * Zugehöriges Deck für das System
     */
    protected Deck deck;

    /**
     * Einzelne Boxen des Systems, die Karten enthalten
     */
    protected ArrayList<Set<Card> > boxes;

    /**
     * Zugehöriger Typ des Systems
     */
    protected StudySystemType type;

    /**
     * Konstruktor der Klasse StudySystem
     * @param deck: Deck des StudySystems
     * @param type: Typ des StudySystems
     * @param nboxes: Anzahl der Boxen für das StudySystem
     */
    public StudySystem(Deck deck, StudySystemType type, int nboxes)
    {
        this.deck = deck;
        this.type = type;
        this.boxes = initArray(nboxes);
    }

    /**
     * Initiiert die Boxen des StudySystems
     * @param size: Übergebene Anzahl der Boxen für das StudySystem
     * @return Boxliste des StudySystems
     */
    private ArrayList<Set<Card> > initArray(int size)
    {
        ArrayList<Set<Card> > retArr = new ArrayList<>();
        for(int i = 0; i < size; i++)
            retArr.add(new HashSet<Card>());

        return retArr;
    }

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems
     * @param card: Zu verschiebene Karte
     * @param boxindex: Index der Box, in den die Karte verschoben werden soll
     */
    public void moveCardToBox(Card card, int boxindex)
    {
        if(boxindex >= boxes.size() || boxindex < 0)
            return;

        for(Set<Card> set : boxes)
        {
            if(set.contains(card))
                set.remove(card);
        }

        boxes.get(boxindex).add(card);
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards){
        boxes.get(0).addAll(cards);
    }

    public ArrayList<Card> getAllCardsInStudySystem(){
        ArrayList<Card> cardsInStudyS = new ArrayList<>();

        for(Set<Card> set : boxes){
            cardsInStudyS.addAll(set);
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