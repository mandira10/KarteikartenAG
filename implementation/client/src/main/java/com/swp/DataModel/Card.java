package com.swp.DataModel;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import static com.swp.Validator.checkNotNullOrBlank;

/**
 * Abstrakte Superklasse für die Karten. Enthält die einzelnen Kartentypen sowie
 * weitere generische Eigenschaften jeder Karte, die alle Untertypen erben.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "CardType")
@Setter
@Getter
@NamedQuery(name  = "Card.findCardByUUID",
            query = "SELECT c FROM Card c WHERE c.uuid = :uuid")
@NamedQuery(name  = "Card.findByTitle",
            query = "SELECT c FROM Card c WHERE c.title = :title ")
@NamedQuery(name  = "Card.allCardNextLearnedAtOlderThanNow",
            query = "SELECT c FROM Card c WHERE c.nextLearnedAt < :now")
//query = "SELECT c FROM Card c WHERE c.uuid IN (SELECT b2c.card FROM BoxToCard b2c WHERE b2c.studySystemBox = :studySystemBox AND b2c.nextLearnDate <= :now ORDER BY ASC b2c.nextLearnDate)")

public abstract class Card implements Serializable
{
    /**
     * Enum für den Kartentypen
     */
    public enum CardType
    {
        TRUEFALSE,
        IMAGETEST,
        IMAGEDESC,
        MULITPLECHOICE,
        TEXT,
        AUDIO
    }

    /**
     * UUID der Karte
     */
    @Id
    @Column
    @Setter(AccessLevel.NONE)
    protected final String uuid;

    /**
     * Kartentyp der spezifischen Karte
     */
    @Setter(AccessLevel.NONE)
    protected final CardType type;

    /**
     * Frage der Karte
     */
    protected String question;

    /**
     * Rating der Karte. Kann der Nutzer vergeben, um Karten (inhaltlich) zu bewerten.
     */
    @Column
    protected int rating;

    /**
     * String Aggregat des Inhaltes. Wird aus den String Inhalten der Karte zusammengestellt.
     * Wird verwendet für die Filterung nach Suchbegriffen.
     */
    @Column
    protected String content;

    //NEU
    @Column
    protected String references;

    /**
     * Titel der Karte. Ist optional, wird verwendet im Glossar, wenn befüllt
     */
    @Column
    protected String title;

    /**
     * Erstellzeitpunkt der Karte.
     */
    @Column
    protected Timestamp creationDate;


    /**
     * Sichtbarkeit der Karte, wenn wahr für alle sichtbar, ansonsten privat
     */
    @Column
    protected boolean visibility;

    /**
     * Zeitpunkt, wann die Karte das nächste Mal gelernt werden soll.
     */
    @Column
    protected Timestamp nextLearnedAt;

    /**
     * Konstruktor für eine einfache Karte.
     * @param type: Karteikartentyp der zu erstellenden Karte.
     */
    public Card(CardType type)
    {
        this.uuid = UUID.randomUUID().toString();
        this.type = type;
        this.rating = 0;
        this.title = "";
        this.content = "";
        this.references = "";
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.nextLearnedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Leerer Konstruktor für eine einfache Karte.
     */
    public Card()
    {
        uuid = UUID.randomUUID().toString();
        this.type = null;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.nextLearnedAt = new Timestamp(System.currentTimeMillis() );
    }


    /**
     * Methode für das Kopieren einer Karte. Hilfsmethode fürs
     * Updaten einer Karte.
     * @param card: Zu kopierende Karte
     * @return Kopie der Karte
     */
    public static Card copyCard(Card card)
    {
        Card retCard = card;
        return retCard;
    }

    public void setContent(){}

    /**
     * Manueller Setter, der prüft, ob der übergebene Wert im GUI leer ist
     * @param question: Frage der Karte
     */
    public void setQuestion(String question) 
    {

            this.question = checkNotNullOrBlank(question,"Frage",false);
    }

    public void setTitle(String title) 
    {
        if(title != null)
            this.title = title;
    }

    public String getAnswerString() { return ""; }


}
