package com.swp.DataModel;

import java.util.UUID;

/**
 * Klasse für einen Tag für Karteikarten
 */
public class Tag 
{
    /**
     * Value des Tags
     */
    private String sValue;

    /**
     * UUID des Tags
     */
    private final String sUUID;

    /**
     * Konstruktor der Klasse Tag
     * @param val: Name des Tags
     */
    public Tag(String val)
    {
        this.sValue = val;
        this.sUUID = UUID.randomUUID().toString();
    }

    //
    // Getter
    //
    public String getValue() { return this.sValue; }
    public String getUUID()  { return this.sUUID; }

    public void setValue(String sValue) {
        this.sValue = sValue;
    }
}
