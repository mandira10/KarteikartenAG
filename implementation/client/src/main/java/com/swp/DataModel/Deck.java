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
<<<<<<< HEAD
        this("", null, CardOrder.ALPHABETICAL, false);
        //this.sUUID = UUID.randomUUID().toString();
=======
        this.uuid = UUID.randomUUID().toString();
>>>>>>> f1acf7301c620cd11eca8d45ea8785dc15c03675
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

}
