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

    private int stars;
    /**
     * Konstruktor der Klasse VoteSystem.
     * Initialisiert 2 Boxen für das VoteSystem, die dazu verwendet werden,
     * den Kartenfortschritt zu speichern.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     */
    public VoteSystem(String name, CardOrder cardOrder, int stars) {
        super(name, cardOrder, StudySystemType.VOTE);
        initStudySystemBoxes(new int[]{0,0});
        this.stars = stars;
    }

    public VoteSystem(String name, CardOrder cardOrder) {
        this(name, cardOrder,5);
    }

    public VoteSystem() 
    {
       this("", CardOrder.ALPHABETICAL,5);
    }


}