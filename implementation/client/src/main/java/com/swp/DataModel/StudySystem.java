package com.swp.DataModel;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.swp.Controller.DeckController;
import com.swp.DataModel.CardTypes.AudioCard;

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
    protected Deck pDeck;

    /**
     * Einzelne Boxen des Systems, die Karten enthalten
     */
    protected ArrayList<Set<Card> > alBoxes;

    /**
     * Zugehöriger Typ des Systems
     */
    protected StudySystemType pType;

    /**
     * Konstruktor der Klasse StudySystem
     * @param deck: Deck des StudySystems
     * @param type: Typ des StudySystems
     * @param nboxes: Anzahl der Boxen für das StudySystem
     */
    public StudySystem(Deck deck, StudySystemType type, int nboxes)
    {
        this.pDeck = deck;
        this.pType = type;
        alBoxes = initArray(nboxes);
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
        if(boxindex >= alBoxes.size() || boxindex < 0)
            return;

        for(Set<Card> set : alBoxes)
        {
            if(set.contains(card))
                set.remove(card);
        }

        alBoxes.get(boxindex).add(card);
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param answer: Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(boolean answer) { }

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
            it = DeckController.getCardsInDeck(pDeck).iterator();
        if(it.hasNext())
            return (Card)it.next();
            
        it = null;
        return null;
        /////////////////////////////////////////////////////////////////
    }
}