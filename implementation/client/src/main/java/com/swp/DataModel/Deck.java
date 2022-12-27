package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

/**
 * Klasse für einen Karteikasten.
 */
@Entity
@Table
@Getter
@Setter
public class Deck implements Serializable
{
    /**
     * Enum für die CardOrder des Decks
     */
    public enum CardOrder
    {
        ALPHABETICAL,
        REVERSED_ALPHABETICAL,
        RANDOM
    }

    /**
     * Bezeichnung des Decks
     */
    @Column
    private String sName;

    /**
     * UUID des Decks
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    @Setter(AccessLevel.NONE)
    private final String sUUID;

    /**
     * Gewähltes StudySystem für das spezifische Deck
     */
    @Column
    private StudySystem pStudySystem;

    /**
     * Initiale Reihenfolge des Decks
     */
    @Column
    private CardOrder iOrder;

    /**
     * Sichtbarkeit des Decks. Wenn wahr, dann für alle sichtbar.
     */
    @Column
    private boolean bVisibility;

    /**
     * Leerer Konstruktor der Klasse Deck
     */
    public Deck()
    {
        this.sUUID = UUID.randomUUID().toString();
    }

    /**
     * Kontruktor der Klasse Deck
     * @param name: Name des Decks
     * @param studySystem: StudySystem des Decks
     * @param cardOrder: initiale Reihenfolge
     * @param visibile: Sichtbarkeit des Decks
     */
    public Deck(String name, StudySystem studySystem, CardOrder cardOrder, boolean visibile)
    {
        this.sUUID = UUID.randomUUID().toString();
        sName =  name;
        pStudySystem = studySystem;
        iOrder = cardOrder;
        bVisibility = visibile;
    }

}
