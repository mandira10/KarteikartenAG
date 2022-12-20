package com.swp.DataModel;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Abstrakte Superklasse für die Karten. Enthält die einzelnen Kartentypen sowie
 * weitere generische Eigenschaften jeder Karte, die alle Untertypen erben.
 */
@Setter
@Getter
public abstract class Card
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
    @Setter(AccessLevel.NONE)
    protected final String sUUID;

    /**
     * Kartentyp der spezifischen Karte
     */
    @Setter(AccessLevel.NONE)
    protected final CardType iType;

    /**
     * Rating der Karte. Kann der Nutzer vergeben, um Karten (inhaltlich) zu bewerten.
     */
    protected int iRating;

    /**
     * String Aggregat des Inhaltes. Wird aus den String Inhalten der Karte zusammengestellt.
     * Wird verwendet für die Filterung nach Suchbegriffen.
     */
    protected String sContent;

    /**
     * Titel der Karte. Ist optional, wird verwendet im Glossar, wenn befüllt
     */
    protected String sTitle;

    /**
     * Erstellzeitpunkt der Karte.
     */
    protected long iCreationDate;


    /**
     * Sichtbarkeit der Karte, wenn wahr für alle sichtbar, ansonsten privat
     */
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
