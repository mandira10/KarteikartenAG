package com.swp.DataModel.StudySystem;

import jakarta.persistence.*;
import lombok.AccessLevel;
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
@Table
@DiscriminatorColumn(name = "StudySystemType")
@NamedQuery(name  = "StudySystem.findStudySystemByContent",
        query = "SELECT s FROM StudySystem s WHERE LOWER(s.name) LIKE LOWER(:name)")
@NamedQuery(name  = "StudySystem.getStudySystemByUUID",
        query = "SELECT s FROM StudySystem s WHERE s.uuid = :uuid")
public abstract class StudySystem implements Serializable
{


    /**
     * Primärer Schlüssel für die persistierten `StudySysteme`
     */
    @Id
    @Setter(AccessLevel.NONE)
    private final String uuid;


    protected int questionCount = 0;
    protected int trueAnswerCount = 0;

    protected int resultPoint = 0;

    protected double progress;

    protected String description;

    /**
     * Bezeichnung des StudySystems/Decks
     */
    @Column(unique = true)
    private String name;


    /**
     * Enum für die initiale Ordnung des Decks
     */
    public enum CardOrder
    {
        ALPHABETICAL,
        REVERSED_ALPHABETICAL,
        RANDOM
    }

    /**
     * Die initiale Ordnung der Karten bei Aufruf des Decks.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private CardOrder cardOrder;


    /**
     * Einzelne Boxen des Systems, die Karten enthalten
     */
    @OneToMany (mappedBy = "studySystem",fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<StudySystemBox> boxes = new ArrayList<StudySystemBox>();


    /**
     * Enum für die unterschiedlichen Study System Arten.
     * Es gibt 3 fertige StudySystem sowie ein Custom System, das selbst benannt wird und
     * wo die Parameter ausgewählt werden.
     */
    public enum StudySystemType
    {
        LEITNER,
        TIMING,
        VOTE,
        CUSTOM
    }
    /**
     * Festgelegter Typ des Systems
     */
    @Getter
    protected StudySystemType type;

    /**
     * Boolean, ob ein Typ als Custom vorliegt. Kann für die GUI verwendet werden, um dieses anders darzustellen.
     */
    private boolean custom;

    /**
     * Textuelle Beschreibung des angepassten Lernsystems.
     */
    private String descriptionOfCustom;

    /**
     * Basiskonstruktor des StudySystems mit Grundfunktionen, die jeder Typ hat. Wird in den einzelnen Subklassen über super aufgerufen.
     * @param name Name des Decks
     * @param cardOrder Initiale Reihenfolge der Karten bei Erstaufruf
     * @param type Typ des StudySystems
     */
    public StudySystem(String name, CardOrder cardOrder, StudySystemType type)
    {
        this.uuid = UUID.randomUUID().toString();
        this.cardOrder = cardOrder;
        this.name = name;
        this.type = type;

    }

    /**
     * Leerer Konstruktor für das StudySystem
     */
    public StudySystem() {
        this("", CardOrder.ALPHABETICAL, StudySystemType.LEITNER);
    }

    protected void  initStudySystemBoxes(int size, int[] daysToRelearn){}

    /**
     * Methode für das Kopieren einer Karte. Hilfsmethode fürs
     * Updaten eines StudySystems
     * @param studySystem: Zu kopierendes StudySystem
     * @return Kopie des StudySystems
     */
    public static StudySystem copyStudySystem(StudySystem studySystem)
    {
        StudySystem retStudySystem = studySystem;
        return retStudySystem;
    }

    /**
     * Copy Konstruktor
     */
    public StudySystem(StudySystem other)
    {
        this.uuid        = other.getUuid();
        this.name        = other.getName();
        this.type        = other.getType();
        this.boxes      =   other.getBoxes();
        this.cardOrder   = other.getCardOrder();
        this.trueAnswerCount = other.getTrueAnswerCount();
        this.questionCount = other.getQuestionCount();
    }



    public void incrementQuestionCount(){questionCount++;}

    public void incrementTrueCount(){
        trueAnswerCount++;
    }

    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySystem studySystem = (StudySystem) o;
        return name.equals(studySystem.name);
    }

    public void setDescription(){}
}