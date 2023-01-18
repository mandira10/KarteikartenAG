package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

public class BoxToCard {


    @Id
    //@GeneratedValue
    /**
     * Identifier und Primärschlüssel für
     * Karte-zu-Deck Verbindung
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
     * Zugehöriges Deck
     */
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "studySystembox_id")
    private final StudySystemBox studySystemBox;


    private Timestamp learnedAt;

    /**
     * Status der Karte im Deck. Wird beim Lernen aktualisiert.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private BoxToCard.CardStatus status;

    /**
     * Konstruktor der Klasse CardToDeck
     * @param c: Karte
     * @param ssb: StudySystemBox
     */
    public BoxToCard(Card c, StudySystemBox ssb)
    {
        this.card = c;
        this.studySystemBox = ssb;
        this.id = UUID.randomUUID().toString();
        this.status = BoxToCard.CardStatus.NEW;
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

