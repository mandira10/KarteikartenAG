package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uniqueCardBox",columnNames = {"card_uuid","studySystemBox_id"}))
@Getter
@Setter
@NamedQuery (name = "BoxToCard.allB2CByStudySystem",
        query = "SELECT c FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")

@NamedQuery(name = "BoxToCard.allb2cByCard",
        query = "SELECT b2c FROM BoxToCard b2c WHERE b2c.card = :card")

@NamedQuery(name = "BoxToCard.findSpecificC2C",
        query = "SELECT b2c FROM BoxToCard b2c  LEFT JOIN StudySystemBox sbox ON sbox.id = b2c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem and b2c.card = :card")

@NamedQuery (name = "Card.ByStudySystem",
        query = "SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem AND c.card = :card")

@NamedQuery (name = "BoxToCard.numberOfLearnedCards",
        query = "SELECT COUNT(distinct c.card) FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox " +
                "LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem AND  c.status = 'SKILLED'")

/**
 * Queries für das initiale Lernen bei Random
 */

@NamedQuery (name = "BoxToCard.allByStudySystem",query = "SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox " +
        "ON sbox.id = c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")

/**
 * Queries für das Relearning von Systemen.
 */
@NamedQuery (name = "BoxToCard.allCardNextLearnedAtOlderThanNowAscending",
        query = "SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox " +
                "LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem " +
                "WHERE s.uuid = :studySystem AND c.learnedNextAt < CURRENT_TIMESTAMP() " +
                "ORDER BY c.learnedNextAt ASC, c.status asc")

@NamedQuery (name = "BoxToCard.allCardsForTiming",
        query = "SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox " +
                "LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem AND sbox.boxNumber = 0 ORDER BY c.status ASC")

@NamedQuery (name = "BoxToCard.allCardsSortedByRating",
        query = "SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox " +
                "LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem AND sbox.boxNumber = 0 ORDER BY c.status ASC, c.rating ASC")


/**
 * BoxToCard Klasse. Hier werden alle Karten zur spezifischen StudySystemBox gespeichert.
 */
public class BoxToCard {



    /**
     * Identifier und Primärschlüssel für
     * Karte-zu-studySystemBox Verbindung
     */
    @Id
    protected final String id;

    /**
     * Enum des CardStatus
     * Skilled: Karte wurde erfolgreich gelernt
     * Relearn: Karte muss neu gelernt werden
     * New: Karte ist neu
     */
    public enum CardStatus
    {
        SKILLED,
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
     * Zugehörige Box der Karte, kann gewechselt werden
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


    /**
     * Rating für die BoxToCard. Wird beim Typ Vote gefüllt.
     */
    @Column
    private int rating;


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
    public BoxToCard(Card c, StudySystemBox ssb)
    {
        this.card = c;
        this.studySystemBox = ssb;
        this.id = UUID.randomUUID().toString();
        this.status = BoxToCard.CardStatus.NEW;
        this.learnedNextAt = Timestamp.valueOf(LocalDate.now().atStartOfDay());
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

