package com.swp.DataModel;

public class Tag 
{
    private String sValue;
    private final String sUUID;

    public Tag(String uuid, String val)
    {
        this.sValue = val;
        this.sUUID = uuid;
    }

    
    //
    // Getter
    //
    public String getValue() { return this.sValue; }
    public String getUUID()  { return this.sUUID; }
}
