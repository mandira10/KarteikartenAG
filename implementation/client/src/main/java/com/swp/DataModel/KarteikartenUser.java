package com.swp.DataModel;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Klasse repräsentiert den einzelnen Nutzer des Lernsystems.
 */
@Entity
@Table
public class KarteikartenUser implements Serializable
{
    /**
     * Unique KarteikartenUser ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private final long id;

    /**
     * Name des Nutzers
     */
    @Column
    private final String name;

    /**
     * sessionID des Nutzers
     */
    @Column
    private String sessionID;

    /**
     * Gibt wieder ob Nutzer authentifiziert ist (Server)
     */
    @Column
    private boolean authenticated;

    /**
     * Passwort vom Nutzer als Hash gespeichert
     * @param sPassHash
     */
    @Column
    private String passHash;

    /**
     * Konstruktor der Klasse KarteikartenUser
     * @param username: Name des Nutzers
     */
    public KarteikartenUser(String username)
    {
        this.id = 0;
        this.name = username;
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public KarteikartenUser() {
        this.id = 0;
        this.name = null;
    }

    //Getter

    String getName()    { return name; }
    String getSession() { return sessionID; }
    long getID()        { return id; }

    /**
     * Login-Funktion für den Nutzer
     * @param password
     */
    public void login(String password)
    {
        //Controller.login() ?
    }
}
