package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;

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
    @Column(unique = true)
    private String name;

    /**
     * UUID des Decks
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    @Setter(AccessLevel.NONE)
    private final String uuid;

    /**
     * Gewähltes StudySystem für das spezifische Deck
     */
    @Column
    private StudySystem studySystem;

    /**
     * Initiale Reihenfolge des Decks
     */

    @Column
    @Enumerated(EnumType.STRING)
    private CardOrder cardOrder;

    /**
     * Sichtbarkeit des Decks. Wenn wahr, dann für alle sichtbar.
     */
    @Column
    private boolean visibility;

    /**
     * Leerer Konstruktor der Klasse Deck
     */
    public Deck()
    {
        this("", null, CardOrder.ALPHABETICAL, false);
        studySystem = new LeitnerSystem(this);
    }

    /**
     * Copy Konstruktor
     */
    public Deck(Deck other)
    {
        this.uuid        = other.getUuid();
        this.name        = other.getName();
        this.studySystem = other.getStudySystem();
        this.cardOrder   = other.getCardOrder();
        this.visibility  = other.isVisibility();
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
        this.uuid = UUID.randomUUID().toString();
        this.name =  name;
        this.studySystem = studySystem;
        this.cardOrder = cardOrder;
        this.visibility = visibile;
    }


    @Override
    public boolean equals(Object o){
        if ( this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return name == deck.name;
    }
}
