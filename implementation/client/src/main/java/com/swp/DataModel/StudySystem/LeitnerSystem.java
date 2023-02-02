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
    }

    /**
     * Leerer Konstruktor der Klasse LeitnerSystem
     */
    public LeitnerSystem() {
        this("", CardOrder.ALPHABETICAL);
    }

    public void resetStudySystemBoxes(int size, int[] daysToRelearn){
        if(size>5){
            for(int i = 5; i < size; i++){
                this.boxes.add(new StudySystemBox(this, daysToRelearn[i],i));
            }
        }
        else {
            for(int i = 4; i > size; i--){
                this.boxes.remove(i);
            }
        }
    }

}
