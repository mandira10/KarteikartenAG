package com.swp.DataModel;

public class Deck 
{
    public enum CardOrder
    {
        ALPHABETICAL,
        REVERSED_ALPHABETICAL,
        RANDOM
    };

    private String sName;
    private final String sUUID;
    private StudySystem pStudySystem;
    private CardOrder iOrder;
    private boolean bVisibility;

    public Deck(String uuid)
    {
        this.sUUID = uuid;
    }


    //
    // Getter
    //
    public String getName()           { return this.sName; }
    public String getUUID()           { return this.sUUID; }
}
