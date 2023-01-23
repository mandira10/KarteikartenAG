package com.swp.DataModel.StudySystem;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse für das VoteSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("Vote")
public class VoteSystem extends StudySystem
{

    /**
     * Konstruktor der Klasse VoteSystem.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     * @param visibility: Sichtbarkeit des Systems
     */
    public VoteSystem(String name, CardOrder cardOrder, boolean visibility) {
        super(name, cardOrder, StudySystemType.VOTE, visibility);
        this.boxes.add(new StudySystemBox(this));
    }



    public VoteSystem(VoteSystem other) {
        super(other);
    }

    public VoteSystem() {
       super("",CardOrder.ALPHABETICAL,StudySystemType.VOTE,false);
    }


}