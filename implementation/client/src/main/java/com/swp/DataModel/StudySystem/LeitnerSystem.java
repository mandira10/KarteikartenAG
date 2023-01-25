package com.swp.DataModel.StudySystem;


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

    /**
     * Konstruktor der Klasse LeitnerSystem.
     *
     * @param name:       der Name des Systems
     * @param cardOrder:  CardOrdner, um die Reihenfolge der Karten festzulegen
     * @param visibility: Sichtbarkeit des Systems
     */
    public LeitnerSystem(String name, CardOrder cardOrder, boolean visibility) {
        super(name, cardOrder, StudySystemType.LEITNER, visibility);
        initStudySystemBoxes(5);
    }


    public LeitnerSystem() {
        this("", CardOrder.ALPHABETICAL, false);
    }


    @Override
    protected void  initStudySystemBoxes(int size) {
        List<Integer> daysToLearn = Arrays.asList(new Integer[]{0,1,3,7,14}); //hardcoded not ideal
        for (int i = 0; i < size; i++)
            this.boxes.add(new StudySystemBox(this,daysToLearn.get(i)));

    }

}
