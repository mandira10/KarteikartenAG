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
//@NamedQuery(name = "StudySystemBox.allBoxes",
//        query = "SELECT s FROM StudySystemBox s")
//@NamedQuery(name = "StudySystemBox.allBoxesByStudySystem",
//        query = "SELECT s FROM StudySystemBox s WHERE s.studySystemBox = :studySystemBox")
//@NamedQuery(name = "StudySystemBox.findSpecificBox",
//        query = "SELECT s FROM StudySystemBox s WHERE s.id = :id")
//@NamedQuery(name = "StudySystemBox.numBoxes",
//        query = "SELECT count(*) FROM StudySystemBox s")
//@NamedQuery(name = "StudySystemBox.numBoxesByStudySystem",
//        query = "SELECT count(*) FROM StudySystemBox s WHERE s.studySystemBox = :studySystemBox")

public class StudySystemBox implements Serializable
{
    @Id
    private String id;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "studySystembox_id")
    private StudySystem studySystem;

    @Column
    private int daysToLearnAgain;

    /**
     * Konstruktor um eine neue leere Box für ein Lernsystem anzulegen.
     *
     */
    public StudySystemBox(StudySystem studySystem) {
        this.studySystem = studySystem;
        this.id = UUID.randomUUID().toString();
    }

    public StudySystemBox(StudySystem studySystem, int daysToLearnAgain) {
        this.studySystem = studySystem;
        this.id = UUID.randomUUID().toString();
        this.daysToLearnAgain = daysToLearnAgain;
    }

    public StudySystemBox() {
        this.id = UUID.randomUUID().toString();
    }
}
