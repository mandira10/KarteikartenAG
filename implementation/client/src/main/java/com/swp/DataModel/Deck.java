package com.swp.DataModel;

import java.util.UUID;

/**
 * Klasse für einen Karteikasten.
 */
public class Deck
{
    /**
     * Enum für die CardOrder des Decks
     */
    public enum CardOrder
    {
        ALPHABETICAL,
        REVERSED_ALPHABETICAL,
        RANDOM
    };

    /**
     * Bezeichnung des Decks
     */
    private String sName;

    /**
     * UUID des Decks
     */
    private final String sUUID;

    /**
     * Gewähltes StudySystem für das spezifische Deck
     */
    private StudySystem pStudySystem;

    /**
     * Initiale Reihenfolge des Decks
     */
    private CardOrder iOrder;

    /**
     * Sichtbarkeit des Decks. Wenn wahr, dann für alle sichtbar.
     */
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

    //
    // Setter
    //
    void setStudySystem(StudySystem system) { this.pStudySystem = system; }
    public void setName(String sName) {this.sName = sName;}
    public void setOrder(CardOrder iOrder) {this.iOrder = iOrder;    }
    public void setVisibility(boolean bVisibility) {this.bVisibility = bVisibility;}

    //
    // Getter
    //
    public String getName()                 { return this.sName; }
    public String getUUID()                 { return this.sUUID; }
    public StudySystem getStudySystem(){return this.pStudySystem;}
    public CardOrder getOrder() {return iOrder;}
    public boolean isVisibility() {return bVisibility;}
}
