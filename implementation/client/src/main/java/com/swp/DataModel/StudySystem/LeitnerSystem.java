package com.swp.DataModel.StudySystem;


import com.gumse.gui.Locale;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Leitner")
@Getter
@Setter
public class LeitnerSystem extends StudySystem
{


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
        setDescription();
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


    public LeitnerSystem() {
        this("", CardOrder.ALPHABETICAL);
    }


    @Override
    protected void  initStudySystemBoxes(int size, int[] daysToReLearn) {
        for (int i = 0; i < size; i++)
            this.boxes.add(new StudySystemBox(this, daysToReLearn[i]));

    }

    @Override
    public void setDescription(){
        description = Locale.getCurrentLocale().getString("descriptionLeitner");
    }

}
