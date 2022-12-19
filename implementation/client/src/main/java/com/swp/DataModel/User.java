package com.swp.DataModel;

import jakarta.persistence.*;

/**
 * Klasse repräsentiert den einzelnen Nutzer des Lernsystems.
 */
@Entity
@Table
public class User 
{
    /**
     * Unique User ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "userID")
    private final long lID;

    /**
     * Name des Nutzers
     */
    @Column(name = "name")
    private final String sName;

    /**
     * sessionID des Nutzers
     */
    @Column(name = "sessionID")
    private String sSessionID;

    /**
     * Gibt wieder ob Nutzer authentifiziert ist (Server)
     */
    @Column(name = "autheticated")
    private boolean bAuthenticated;

    /**
     * Passwort vom Nutzer als Hash gespeichert
     * @param sPassHash
     */
    @Column(name = "passHash")
    private String sPassHash;

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
