package com.swp.DataModel;

/**
 * Klasse für die unterschiedlichen StudySystem Typen
 * Neben den drei bereits bestehenden Typen werden alle neu hinzugefügten Systeme
 * als custom System angelegt.
 */
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
    private String sTypeName;

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
            case LEITNER: this.sTypeName = "Leitner"; 
            case TIMING:  this.sTypeName = "Timing"; 
            case VOTE:    this.sTypeName = "Vote"; 
            case CUSTOM:
            default:      this.sTypeName = name;
        }
    }


    //
    // Setter
    //
    void setName(String name) { this.sTypeName = name; }


    //
    // Getter
    //
    String getName() { return this.sTypeName; }
}