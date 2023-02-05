package com.swp.DataModel.StudySystem;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Abstrakte Superklasse für das StudySystem von der die einzelnen
 * Systeme erben
 *  @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
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

    /**
     * Die Anzahl an gelernten Fragen für den
     * aktuellen Lerndurchlauf. Wird beim Beenden
     * oder Abbruch des Testings wieder auf 0 gesetzt.
     * Dient der Berechnung des Ergebnisses (resultPoint, wird nicht in DB gespeichert) pro Lerndurchlauf.
     */
    protected int questionCount;

    /**
     * Die Anzahl an richtig beantworteten Fragen im
     * aktuellen Lerndurchlauf. Wird beim Beenden
     * oder Abbruch des Testings wieder auf 0 gesetzt.
     * Dient der Berechnung des Ergebnisses (resultPoint, wird nicht in DB gespeichert) pro Lerndurchlauf.
     */
    protected int trueAnswerCount;

    /**
     * Der allgemeine Fortschritt des Lernens.
     * Wird in der GUI je LernsystemDeck angezeigt und
     * wird berechnet anhand des Statuses an gelernten Karten
     * zu nicht gelernten / erneut zu lernenb Karten.
     */
    protected double progress;


    /**
     * Gibt an, ob das Lernsystem bereits gelernt wurde,
     * wird im GUI für das Anpassen des Buttons benutzt und
     * in der Logik beim Abrufen der Karten in getNextCard() aufgerufen.
     */
    protected boolean notLearnedYet;

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
        /** Alphabetisch */         ALPHABETICAL,
        /** Umgekehrtes Alphabet */ REVERSED_ALPHABETICAL,
        /** Zufällig */             RANDOM
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
    protected List<StudySystemBox> boxes = new ArrayList<>();


    /**
     * Enum für die unterschiedlichen Study System Arten.
     * Es gibt 3 fertige StudySystem sowie ein Custom System, das selbst benannt wird und
     * wo die Parameter ausgewählt werden.
     */
    public enum StudySystemType
    {
        /** Leitner */   LEITNER,
        /** Zeitlimit */ TIMING,
        /** Bewertung */ VOTE,
        /** Keins */     NONE;

        @Override
        public String toString() 
        {
            String ret = super.toString();
            return ret.substring(0, 1).toUpperCase() + ret.substring(1).toLowerCase();
        }
    }
    
    /**
     * Festgelegter Typ des Systems
     */
    @Getter
    protected StudySystemType type;


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
        setTrueAnswerCount(0);
        setQuestionCount(0);
        setNotLearnedYet(true);
    }

    /**
     * Leerer Konstruktor für das StudySystem
     */
    public StudySystem()
    {
        this("", CardOrder.ALPHABETICAL, StudySystemType.LEITNER);
    }

    /**
     * Basisfunktion für das Initialisieren von StudySystemBoxes.
     * @param daysToRelearn Tage zum Relearnen (wird nur bei Custom LeitnerSystem gesetzt)
     */
    protected void  initStudySystemBoxes(int[] daysToRelearn)
    {
        for (int i = 0; i < daysToRelearn.length; i++)
            this.boxes.add(new StudySystemBox(this, daysToRelearn[i],i));
    }

    /**
     * Methode für das Kopieren einer Karte. Hilfsmethode fürs
     * Updaten eines StudySystems
     * @param studySystem: Zu kopierendes StudySystem
     * @return Kopie des StudySystems
     */
    public static StudySystem copyStudySystem(StudySystem studySystem)
    {
        return studySystem;
    }


    /**
     * erhöht den Zähler, der die Anzahl der enthaltenen Fragen des Lernsystems enthält
     */
    public void incrementQuestionCount()
    {
        questionCount++;
    }

    /**
     * erhöht den Zähler, der die Anzahl der korrekt beantworteten Fragen im Lernsystem enthält
     */
    public void incrementTrueCount()
    {
        trueAnswerCount++;
    }

    @Override
    public boolean equals(Object o)
    {
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudySystem studySystem = (StudySystem) o;
        return name.equals(studySystem.name);
    }
}