package com.swp.DataModel.StudySystem;


import lombok.Getter;
import lombok.Setter;

/**
 * Klasse für das BasicSystem. Erbt alle Attribute vom StudySystem.
 * Kann vom Nutzer erstellt werden und steht dann unter dem Reiter Custom als Lernsystem zur Verfügung.
 */

@Getter
@Setter
public class BasicSystem extends StudySystem
{

    /**
     * Konstruktor der Klasse BasicSystem.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     */
    public BasicSystem(String name, CardOrder cardOrder, int boxes, int[]daysforBoxes) {
        super(name, cardOrder, StudySystemType.LEITNER);
        this.boxes.add(new StudySystemBox(this));
    }




    public BasicSystem() {
        //
    }


}