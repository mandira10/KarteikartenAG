package com.swp.DataModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Klasse für einen Tag für Karteikarten
 */

@Getter
@Setter
public class Tag 
{
    /**
     * Value des Tags
     */
    private String sValue;

    /**
     * UUID des Tags
     */
    @Setter(AccessLevel.NONE)
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
}
