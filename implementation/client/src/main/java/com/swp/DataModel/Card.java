package com.swp.DataModel;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.UUID;

/**
 * Abstrakte Superklasse für die Karten. Enthält die einzelnen Kartentypen sowie
 * weitere generische Eigenschaften jeder Karte, die alle Untertypen erben.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Setter
@Getter
@NamedQuery(name = "Card.findCardByUUID",
            query = "SELECT Card FROM Card WHERE sUUID = :uuid")
@NamedQuery(name = "Card.findCardsByContent",
            query = "SELECT Card FROM Card WHERE LOWER(sContent) LIKE '%LOWER(:content)%' ")
public abstract class Card implements Serializable
{
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
    }

    /**
     * UUID der Karte
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column
    @Setter(AccessLevel.NONE)
    protected final String sUUID;

    /**
     * Kartentyp der spezifischen Karte
     */
    @Column
    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    protected final CardType iType;

    /**
     * Frage der Karte
     */
    protected String sQuestion;

    /**
     * Rating der Karte. Kann der Nutzer vergeben, um Karten (inhaltlich) zu bewerten.
     */
    @Column
    protected int iRating;

    /**
     * String Aggregat des Inhaltes. Wird aus den String Inhalten der Karte zusammengestellt.
     * Wird verwendet für die Filterung nach Suchbegriffen.
     */
    @Column
    protected String sContent;

    /**
     * Titel der Karte. Ist optional, wird verwendet im Glossar, wenn befüllt
     */
    @Column
    protected String sTitle;

    /**
     * Erstellzeitpunkt der Karte.
     */
    @Column
    protected long iCreationDate;


    /**
     * Sichtbarkeit der Karte, wenn wahr für alle sichtbar, ansonsten privat
     */
    @Column
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
     * Leerer Konstruktor für eine einfache Karte.
     */
    public Card()
    {
        this.sUUID = UUID.randomUUID().toString();
        this.iType = null;
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
