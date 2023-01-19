package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Entity
@Setter
@Table
@Getter
@NamedQuery(name = "StudySystemBox.allBoxes",
        query = "SELECT s FROM StudySystemBox s")
@NamedQuery(name = "StudySystemBox.allBoxesByStudySystem",
        query = "SELECT s FROM StudySystemBox s WHERE s.studySystemBox = :studySystemBox")
@NamedQuery(name = "StudySystemBox.findSpecificBox",
        query = "SELECT s FROM StudySystemBox s WHERE s.id = :id")
@NamedQuery(name = "StudySystemBox.numBoxes",
        query = "SELECT count(*) FROM StudySystemBox s")
@NamedQuery(name = "StudySystemBox.numBoxesByStudySystem",
        query = "SELECT count(*) FROM StudySystemBox s WHERE s.studySystemBox = :studySystemBox")

public class StudySystemBox implements Serializable
{
    @Id
    private String id;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Card> boxContent;

    /**
     * Zugehöriges Deck
     */
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "studySystembox_id")
    private final StudySystemBox studySystemBox;
    /**
     * Konstruktor um eine neue interne Box für ein Lernsystem anzulegen.
     *
     */
    public StudySystemBox(List<Card> cards) {
        this.id = UUID.randomUUID().toString();
        this.boxContent = cards;
        this.studySystemBox = null;
    }

    /**
     * Konstruktor um eine neue leere Box zu erstellen
     */
    public StudySystemBox() {
        this.id = UUID.randomUUID().toString();
        this.boxContent = new ArrayList<>();
        this.studySystemBox = null;
    }

    /**
     * Funktion um eine einzelne Karte zu dem bestehenden Set zuzufügen.
     * @param card Die Karte die in das `boxContent` Set zugefügt werden soll.
     */
    public void add(final Card card) {
        boxContent.add(card);
    }

    /**
     * Funktion um mehrere Karten zum internen Set zuzufügen.
     * @param cards Die Liste der zuzufügenden Karten.
     */
    public void add(final List<Card> cards) {
        boxContent.addAll(cards);
    }

    /**
     * Funktion um eine einzelne Karte aus dem bestehenden Set zu entfernen.
     * @param card Die Karte die aus dem `boxContent` Set entfernt werden soll.
     */
    public void remove(final Card card) {
        boxContent.remove(card);
    }
}
