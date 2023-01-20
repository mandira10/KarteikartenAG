package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uniqueCardBox",columnNames = {"card_uuid","studySystemBox_id"}))
@Getter
@Setter
@NamedQuery(name = "BoxToCard.allC2DByCard",
        query = "SELECT b2c.studySystemBox FROM BoxToCard b2c WHERE b2c.card = :card")
@NamedQuery(name = "BoxToCard.allb2cByCard",
        query = "SELECT b2c FROM BoxToCard b2c WHERE b2c.card = :card")
@NamedQuery(name = "BoxToCard.allB2CByStudySystem",
        query = "SELECT b2c FROM BoxToCard b2c LEFT JOIN StudySystemBox sbox ON sbox.id = b2c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")
@NamedQuery(name = "BoxToCard.findSpecificC2C",
        query = "SELECT b2c FROM BoxToCard b2c  LEFT JOIN StudySystemBox sbox ON sbox.id = b2c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem and b2c.card = :card")
@NamedQuery(name = "BoxToCard.numCardsInStudySystem",
        query = "SELECT count(distinct c.id) FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")
@NamedQuery(name = "BoxToCard.numstudySystemBoxsWithCard",
        query = "SELECT count(distinct c.id) FROM BoxToCard c WHERE c.card = :card")
public class BoxToCard {


    @Id
    /**
     * Identifier und Primärschlüssel für
     * Karte-zu-studySystemBox Verbindung
     */
    protected final String id;

    /**
     * Enum des CardStatus
     */
    public enum CardStatus
    {
        LEARNED,
        RELEARN,
        NEW
    }

    /**
     * Zugehörige Karte
     */
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "card_uuid")
    private final Card card;

    /**
     * Zugehörige Box der Karte, kann angepasst werden.
     */
    @ManyToOne
    @JoinColumn(name = "studySystemBox_id")
    private StudySystemBox studySystemBox;


    /**
     * Zeitpunkt des nächsten Lernens der Karte. Ist abhängig vom StudySystem, wird anfangs immer auf
     * bei Erstellung einer neuen BoxToCard immer auf das aktuelle Datum gesetzt.
     * Deswegen kann auch die Box einer Referenz manuell geupdatet werden.
     */
    @Column
    private Timestamp learnedNextAt;

    @Column
    private int boxNumber;

    /**
     * Status der Karte im studySystemBox. Wird beim Lernen aktualisiert.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private BoxToCard.CardStatus status;

    /**
     * Konstruktor der Klasse BoxToCard
     * @param c: Karte
     * @param ssb: StudySystemBox
     */
    public BoxToCard(Card c, StudySystemBox ssb,  int boxNumber)
    {
        this.card = c;
        this.studySystemBox = ssb;
        this.boxNumber = boxNumber;
        this.id = UUID.randomUUID().toString();
        this.status = BoxToCard.CardStatus.NEW;
        this.learnedNextAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public BoxToCard() {
        this.card = null;
        this.studySystemBox = null;
        this.id = UUID.randomUUID().toString();
    }

}

