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
     * Enum für die CardOrder des Decks
     */
    public enum CardOrder
    {
        ALPHABETICAL,
        REVERSED_ALPHABETICAL,
        RANDOM
    }

    /**
     * Bezeichnung des Decks
     */
    @Column(unique = true)
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private CardOrder cardOrder;

    /**
     * Sichtbarkeit des Decks. Wenn wahr, dann für alle sichtbar.
     */
    @Column
    private boolean visibility;

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
     * @param ...
     * @param type: Typ des StudySystems
     * @param nboxes: Anzahl der Boxen für das StudySystem
     */
    public StudySystem(String name,CardOrder cardOrder, StudySystemType type, int nboxes, boolean visibility)
    {
        this.id = UUID.randomUUID().toString();
        this.cardOrder = cardOrder;
        this.visibility = visibility;
        this.name = name;
        this.type = type;
        initStudySystemBoxes(nboxes);

    }

    // No-Arg Konstruktor
    public StudySystem() {
        this("",null,null, 0, false);
    }

    /**
     * Initiiert die Boxen des StudySystems
     * @param size: Übergebene Anzahl der Boxen für das StudySystem
     * @return Boxliste des StudySystems
     */
    private void  initStudySystemBoxes(int size) {
      //  for (int i = 0; i < size; i++)
        //  this.boxes.add(new StudySystemBox(this));
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



    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySystem studySystem = (StudySystem) o;
        return name == studySystem.name;
    }
}