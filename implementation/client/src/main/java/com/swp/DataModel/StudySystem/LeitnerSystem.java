package com.swp.DataModel.StudySystem;


import com.gumse.gui.Locale;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;


@Entity
@DiscriminatorValue("Leitner")
@Getter
@Setter
/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
public class LeitnerSystem extends StudySystem
{

    /**
     * daysToRelearn, wird bei Custom Leitner Systemen in der GUI gesetzt
     * und beim Lernen für die Wiederholung der Boxen verwendet.
     */
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column
    private int[] daysToRelearn;


    /**
     * Konstruktor der Klasse LeitnerSystem.
     *
     * @param name       der Name des Systems
     * @param cardOrder  CardOrdner, um die Reihenfolge der Karten festzulegen
     */
    public LeitnerSystem(String name, CardOrder cardOrder) {
        super(name, cardOrder, StudySystemType.LEITNER);
        initStudySystemBoxes(5, new int[]{0,1,3,7,14});
        setCustom(false);
    }


    /**
     * Konstruktor für ein custom Leitner System. Neben der Beschreibung wird hier auch noch die Anzahl der Boxen
     * sowie die zugehörigen Lerntage festgelegt.
     * @param name der Name des StudySystems
     * @param cardOrder die initiale Lernreihenfolge
     * @param boxes Die Anzahl der Boxen
     * @param daysToRelearn die übergebenen Tage, wann gelernt werden soll
     * @param description Die Beschreibung des Custom Systems
     */
    public LeitnerSystem(String name, CardOrder cardOrder, int boxes, int[] daysToRelearn, String description) {
        super(name, cardOrder, StudySystemType.LEITNER);
        initStudySystemBoxes(boxes,daysToRelearn);
        setCustom(true);
        setDescriptionOfCustom(description);
    }

    /**
     * Leerer Konstruktor der Klasse LeitnerSystem
     */
    public LeitnerSystem() {
        this("", CardOrder.ALPHABETICAL);
    }

}
