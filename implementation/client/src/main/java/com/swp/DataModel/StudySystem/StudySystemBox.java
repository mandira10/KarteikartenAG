package com.swp.DataModel.StudySystem;

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
        query = "SELECT s FROM StudySystemBox s WHERE s.studySystem = :studySystem")
@NamedQuery(name = "StudySystemBox.findSpecificBox",
        query = "SELECT s FROM StudySystemBox s WHERE s.id = :id")
/**
 * StudySystemBox Klasse. Wird verwendet, wenn ein Lernsystem mehrere Boxen hat (z.B. Leitner),
 * deren Karten unterschiedlich oft gelernt werden.
 * Wird in BoxToCard an die spezifischen Karten gebunden.
 */
public class StudySystemBox implements Serializable
{

    /**
     * Id der StudySystemBox
     */
    @Id
    private String id;

    /**
     * Das zugehörige StudySystem
     * Mehrere Boxen (Leitner) System gehören zu einem StudySystem.
     */
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "studySystem_id")
    private StudySystem studySystem;

    /**
     * Anzahl der Tage, ab wann die Box erneut wieder gelernt werden muss (Leitner)
     */
    @Column
    private int daysToLearnAgain;

    /**
     * Spezifische Box Nummer für die StudySystemBox der BoxToCard.
     * Wird beim Lernen genutzt, um die Karten in die richtige Box zu verschieben.
     */
    @Column
    private int boxNumber;

    /**
     * Konstruktor um eine neue leere Box für ein Lernsystem anzulegen.
     *
     */
    public StudySystemBox(StudySystem studySystem) {
        this.studySystem = studySystem;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Standardkonstruktor für die StudySystemBox
     * @param studySystem das zugehörige StudySystem für die Box
     * @param daysToLearnAgain Angabe, in welchen Abschnitten die Box wieder gelernt werden muss.
     */
    public StudySystemBox(StudySystem studySystem, int daysToLearnAgain, int boxNumber) {
        this.studySystem = studySystem;
        this.id = UUID.randomUUID().toString();
        this.daysToLearnAgain = daysToLearnAgain;
        this.boxNumber = boxNumber;
    }

    /**
     * Leerer Konstruktor der StudySystemBox
     */
    public StudySystemBox() {
        this.id = UUID.randomUUID().toString();
    }
}
