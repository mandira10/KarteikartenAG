package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

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
    protected int questionCount = 0;
    protected int trueAnswerCount = 0;
    protected int pointQuestion = 100;
    protected int resultPoint;

    /**
     * Primärer Schlüssel für die persistierten `StudySysteme`
     */
    @Id
    private final String id;

    /**
     * Zugehöriges Deck für das System
     */
    @OneToOne
    @JoinColumn (name="deck")
    protected Deck deck;

    /**
     * Einzelne Boxen des Systems, die Karten enthalten
     */
    @OneToMany (mappedBy = "studySystem",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<StudySystemBox> boxes = new ArrayList<>(); //ArrayList funktioniert nicht, man muss generelle Typen nehmen

    /**
     * Zugehöriger Typ des Systems
     */
    @Getter
    protected StudySystemType type;

    public enum StudySystemType
    {
        LEITNER,
        TIMING,
        VOTE,
        CUSTOM
    }

    /**
     * Konstruktor der Klasse StudySystem
     * @param deck: Deck des StudySystems
     * @param type: Typ des StudySystems
     * @param nboxes: Anzahl der Boxen für das StudySystem
     */
    public StudySystem(Deck deck, StudySystemType type, int nboxes)
    {
        this.deck = deck;
        deck.setStudySystem(this);
        this.type = type;
        initStudySystemBoxes(nboxes);
        this.id = UUID.randomUUID().toString();
    }

    // No-Arg Konstruktor
    public StudySystem() {
        this(null, null, 0);
    }

    /**
     * Initiiert die Boxen des StudySystems
     * @param size: Übergebene Anzahl der Boxen für das StudySystem
     * @return Boxliste des StudySystems
     */
    private void  initStudySystemBoxes(int size) {
        for (int i = 0; i < size; i++)
          this.boxes.add(new StudySystemBox(this));
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

        for(StudySystemBox box : boxes)
        {
            if(box.getBoxContent().contains(card))
                box.remove(card);
        }

        boxes.get(boxindex).add(card);
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
    public Card getNextCard(int index)
    {
        for (StudySystemBox box : boxes) {
            if (!box.getBoxContent().isEmpty()){
                return box.getBoxContent().iterator().next();
            }
        }
        throw new NoResultException("No Cards in StudySystem");
    }

    //NEEDS TO BE IMPLEMENTED
    public float getProgress()
    {
        return (float)Math.random();  //Should return percentage as: 0.0 ... 1.0
    }
}