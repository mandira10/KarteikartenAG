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
    protected int pointQuestion = 100;
    protected int resultPoint;

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
     * Bezeichnung des StudySystems/Decks
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
    protected List<StudySystemBox> boxes = new ArrayList<StudySystemBox>();

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
    * //TODO
     */
    public StudySystem(String name, CardOrder cardOrder, StudySystemType type, int nboxes, boolean visibility)
    {
        this.uuid = UUID.randomUUID().toString();
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
        this.visibility  = other.isVisibility();
        this.trueAnswerCount = other.getTrueAnswerCount();
        this.questionCount = other.getQuestionCount();
        this.pointQuestion = other.getPointQuestion();
        this.resultPoint = other.getResultPoint();
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

    public void incrementQuestionCount(){questionCount++;}

    public void incrementTrueCount(){
        trueAnswerCount++;
    }

    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySystem studySystem = (StudySystem) o;
        return name == studySystem.name;
    }
}