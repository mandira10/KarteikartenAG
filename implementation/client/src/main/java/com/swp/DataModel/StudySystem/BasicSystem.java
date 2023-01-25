package com.swp.DataModel.StudySystem;

/**
 * Klasse für das BasicSystem. Erbt alle Attribute vom StudySystem
 */

// TODO
public class BasicSystem extends StudySystem
{

    /**
     * Konstruktor der Klasse BasicSystem.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     * @param visibility: Sichtbarkeit des Systems
     */
    public BasicSystem(String name, CardOrder cardOrder, boolean visibility) {
        super(name, cardOrder, StudySystemType.CUSTOM, visibility);
        this.boxes.add(new StudySystemBox(this));
    }



    public BasicSystem(BasicSystem other) {
        super(other);
    }

    public BasicSystem() {
        //
    }


}