package com.swp.DataModel.StudySystem;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse für das TimingSystem. Erbt alle Attribute vom StudySystem
 *  @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("Timing")
public class TimingSystem extends StudySystem {
    /**
     * Zeitlimit für jede Frage zu beantworten
     */
    private float timeLimit = 0;

    /**
     * Konstruktor der Klasse TimingSystem.
     * Initialisiert 2 Boxen für das TimingSystem, die dazu verwendet werden,
     * den Kartenfortschritt zu speichern.
     *
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     * @param timeLimit: Zeitlimit für jede Frage zu beantworten
     */
    public TimingSystem(String name, CardOrder cardOrder, int timeLimit) {
        super(name, cardOrder, StudySystemType.TIMING);
        initStudySystemBoxes(new int[]{0, 0});
        this.timeLimit = timeLimit;
    }

    /**
     * Leerer Konstruktor für das Initialiseren eines Standardlernsystems.
     * Wird in der GUI aufgerufen. Übergibt standardmäßig 30 Sekunden als Timelimit, kann
     * vom User angepasst werden.
     */
    public TimingSystem() {
        this("", CardOrder.ALPHABETICAL, 30);
    }


}


