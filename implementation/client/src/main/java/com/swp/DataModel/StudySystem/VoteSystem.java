package com.swp.DataModel.StudySystem;

import com.gumse.gui.Locale;
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
     * Initialisiert 2 Boxen für das VoteSystem, die dazu verwendet werden,
     * den Kartenfortschritt zu speichern.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     */
    public VoteSystem(String name, CardOrder cardOrder) {
        super(name, cardOrder, StudySystemType.VOTE);
        initStudySystemBoxes(2,new int[]{0,0});
        setDescription();
    }


    public VoteSystem() {
       this("",CardOrder.ALPHABETICAL);
    }

    @Override
    public void setDescription(){
        description = Locale.getCurrentLocale().getString("descriptionVote");
    }

}