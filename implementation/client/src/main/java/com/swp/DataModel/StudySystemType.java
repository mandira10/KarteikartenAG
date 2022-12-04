package com.swp.DataModel;

public class StudySystemType
{
    public enum KNOWN_TYPES 
    {
        LEITNER,
        TIMING,
        VOTE,
        CUSTOM
    }

    private String sTypeName;

    public StudySystemType(KNOWN_TYPES type) { this(type, ""); }
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