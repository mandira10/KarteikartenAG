package com.swp.DataModel;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Klasse repräsentiert den einzelnen Nutzer des Lernsystems.
 */
@Entity
@Table
public class User implements Serializable
{
    /**
     * Unique User ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private final long lID;

    /**
     * Name des Nutzers
     */
    @Column
    private final String sName;

    /**
     * sessionID des Nutzers
     */
    @Column
    private String sSessionID;

    /**
     * Gibt wieder ob Nutzer authentifiziert ist (Server)
     */
    @Column
    private boolean bAuthenticated;

    /**
     * Passwort vom Nutzer als Hash gespeichert
     * @param sPassHash
     */
    @Column
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

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public User() {
        this.lID = 0;
        this.sName = null;
    }

    //Getter

    String getName()    { return sName; }
    String getSession() { return sSessionID; }
    long getID()        { return lID; }

    /**
     * Login-Funktion für den Nutzer
     * @param password
     */
    public void login(String password)
    {
        //Controller.login() ?
    }
}
