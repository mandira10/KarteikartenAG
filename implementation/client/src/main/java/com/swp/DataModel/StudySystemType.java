package com.swp.DataModel;

import lombok.Getter;
import lombok.Setter;

/**
 * Klasse für die unterschiedlichen StudySystem Typen
 * Neben den drei bereits bestehenden Typen werden alle neu hinzugefügten Systeme
 * als custom System angelegt.
 */
@Getter
@Setter
public class StudySystemType
{
    /**
     * Enum für die unterschiedlichen Typen
     */
    public enum KNOWN_TYPES 
    {
        LEITNER,
        TIMING,
        VOTE,
        CUSTOM
    }

    /**
     * Name des Systems. Kann bei neu angelegten Systemen hinzugefügt werden
     */
    private String typeName;

    /**
     * Konstruktor der Klasse StudySystemType ohne Angabe eines Namens
     * @param type: übergebender StudySystemType
     */
    public StudySystemType(KNOWN_TYPES type) { this(type, ""); }

    /**
     * Konstruktor der Klasse StudySystemType mit Typ und Namen
     * @param type: Typ des StudySystems
     * @param name: Name des StudySystems
     */
    public StudySystemType(KNOWN_TYPES type, String name)
    {
        switch(type)
        {
            case LEITNER: this.typeName = "Leitner";
            case TIMING:  this.typeName = "Timing";
            case VOTE:    this.typeName = "Vote";
            case CUSTOM:
            default:      this.typeName = name;
        }
    }

}