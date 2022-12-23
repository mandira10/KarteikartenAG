package com.swp.DataModel;


import jakarta.persistence.*;
import java.util.UUID;

/**
 * Abstrakte Superklasse für die Karten. Enthält die einzelnen Kartentypen sowie
 * weitere generische Eigenschaften jeder Karte, die alle Untertypen erben.
 */
@Entity
@Table
public abstract class Card
{
    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public Card() {}

    /**
     * Enum für den Kartentypen
     */
    // @Column(name = "type")
    // funktioniert nicht, man könnte z.B. einen String persisten und mit einer HashMap hin und her übersetzen
    // siehe: https://stackoverflow.com/a/492126
    public enum CardType
    {
        TRUEFALSE,
        IMAGETEST,
        IMAGEDESC,
        MULITPLECHOICE,
        TEXT,
        AUDIO
    };

    /**
     * UUID der Karte
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    protected final String sUUID;

    /**
     * Kartentyp der spezifischen Karte
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    protected final CardType iType;

    /**
     * Rating der Karte. Kann der Nutzer vergeben, um Karten (inhaltlich) zu bewerten.
     */
    @Column(name = "rating")
    protected int iRating;

    /**
     * String Aggregat des Inhaltes. Wird aus den String Inhalten der Karte zusammengestellt.
     * Wird verwendet für die Filterung nach Suchbegriffen.
     */
    @Column(name = "content")
    protected String sContent;

    /**
     * Titel der Karte. Ist optional, wird verwendet im Glossar, wenn befüllt
     */
    @Column(name = "title")
    protected String sTitle;

    /**
     * Erstellzeitpunkt der Karte.
     */
    @Column(name = "creationDate")
    protected long iCreationDate;


    /**
     * Sichtbarkeit der Karte, wenn wahr für alle sichtbar, ansonsten privat
     */
    @Column(name = "visibility")
    protected boolean bVisibility;

    /**
     * Konstruktor für eine einfache Karte.
     * @param type: Karteikartentyp der zu erstellenden Karte.
     */
    public Card(CardType type)
    {
        this.sUUID = UUID.randomUUID().toString();
        this.iType = type;
        this.iRating = 0;
        this.sTitle = "";
        this.sContent = "";
        this.iCreationDate = System.currentTimeMillis();
    }

    /**
     * Setter für das Rating
     * @param rating: Übergebene Bewertung
     */
    public void setRating(int rating)  { this.iRating = rating; }

    /**
     * Fügt der Karte einen Titel hinzu.
     * @param title: Titel der Karte
     */
    public void setTitle(String title) { this.sTitle = title; }

    /**
     * Ändert die Sichtbarkeit der Karte
     * @param bVisibility
     */
    public void setVisible(boolean bVisibility) {
        this.bVisibility = bVisibility;
    }
    /**
     * Gibt den Karteikartentyp zurück.
     * @return iType
     */
    public CardType getType() { return iType; }

    /**
     * Gibt die UUID der Karte zurück.
     * @return sUUID
     */
    public String getUUID()   { return sUUID; }

    /**
     * Gibt den textuellen Inhalt einer Karte zurück.
     * @return sContent
     */
    public String getContent(){ return sContent; }

    /**
     * Gibt den Titel einer Karte zurück.
     * @return sTitle
     */
    public String getTitle()  { return sTitle; }

    /**
     * Gibt den Erstellzeitpunkt der Karte zurück.
     * @return CreationDate
     */
    public long getCreationDate() {
        return iCreationDate;
    }

    /**
     * Gibt die Sichtbarkeit der Karte zurück
     * @return bVisibility
     */
    public boolean isVisible() {
        return bVisibility;
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
        /*switch(card.iType)
        {
            case AUDIO: retCard = new AudioCard((AudioCard)card); break;
            case IMAGEDESC:
                break;
            case IMAGETEST:
                break;
            case MULITPLECHOICE:
                break;
            case TEXT:
                break;
            case TRUEFALSE:
                break;
            default:
                break;

        }*/

        return retCard;
    }
}
