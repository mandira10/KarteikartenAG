package com.swp.DataModel.StudySystem;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 *  @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
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
     * @param name       der Name des Systems
     * @param cardOrder  CardOrdner, um die Reihenfolge der Karten festzulegen
     */
    public LeitnerSystem(String name, CardOrder cardOrder) {
        super(name, cardOrder, StudySystemType.LEITNER);
        initStudySystemBoxes(new int[]{0,1,3,7,14});
    }

    /**
     * Leerer Konstruktor der Klasse LeitnerSystem
     */
    public LeitnerSystem() {
        this("", CardOrder.ALPHABETICAL);
    }


}
