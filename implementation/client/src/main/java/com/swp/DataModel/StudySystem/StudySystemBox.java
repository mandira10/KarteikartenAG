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
@Getter
public class StudySystemBox implements Serializable
{
    @Id
    private String id;

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Card> boxContent;


    @ManyToOne
    @JoinColumn(name = "studySystem")
    private StudySystem studySystem;

    /**
     * Konstruktor um eine neue interne Box für ein Lernsystem anzulegen.
     *
     */
    public StudySystemBox(StudySystem studySystem, LinkedHashSet<Card> cards) {
        this.boxContent = cards;
    }
    public StudySystemBox(StudySystem studySystem) {
        this.id = UUID.randomUUID().toString();
        this.studySystem = studySystem;
        this.boxContent = new HashSet<>();
    }

    /**
     * Konstruktor um eine neue leere Box zu erstellen
     */
    public StudySystemBox() {
        this.id = UUID.randomUUID().toString();
        this.boxContent = new HashSet<>();
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
