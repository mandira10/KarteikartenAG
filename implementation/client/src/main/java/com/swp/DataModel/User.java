package com.swp.DataModel;

/**
 * Klasse repräsentiert den einzelnen Nutzer des Lernsystems.
 */
public class User 
{
    /**
     * Unique User ID
     */
    private final long lID;

    /**
     * Name des Nutzers
     */
    private final String sName;

    /**
     * sessionID des Nutzers
     */
    private String sSessionID;

    /**
     * Gibt wieder ob Nutzer authentifiziert ist (Server)
     */
    private boolean bAuthenticated;

    /**
     * Konstruktor der Klasse User
     * @param username: Name des Nutzers
     */
    public User(String username)
    {
        this.lID = 0;
        this.sName = username;
    }

 //Getter

    String getName()    { return sName; }
    String getSession() { return sSessionID; }
    long getID()        { return lID; }

    /**
     * Loginfunktion für den Nutzer
     * @param password
     */
    public void login(String password)
    {
        //Controller.login() ?
    }
}
