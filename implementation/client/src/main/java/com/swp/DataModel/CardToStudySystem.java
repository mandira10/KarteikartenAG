package com.swp.DataModel;

import com.swp.DataModel.StudySystem.StudySystem;
import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;

@Entity
@Table
@NamedQuery(name  = "CardToStudySystem.findSpecificC2S",
            query = "SELECT c FROM CardToStudySystem c WHERE card = :card AND studySystem = :studySystem")
@NamedQuery(name  = "CardToStudySystem.getAllC2SForStudySystem",
            query = "SELECT c FROM CardToStudySystem c WHERE studySystem = :studySystem")
@NamedQuery(name  = "CardToStudySystem.getCardsFromSpecificStudySystemBox",
            query = "SELECT c.card FROM CardToStudySystem c WHERE studySystem = :studySystem AND box = :boxNr")
@NamedQuery(name  = "CardToStudySystem.getAllCardsFromSpecificStudySystem",
        query = "SELECT c.card FROM CardToStudySystem c WHERE studySystem = :studySystem")
@NamedQuery(name  = "CardToStudySystem.getBoxNrForC2SByID",
            query = "SELECT c.box FROM CardToStudySystem c WHERE id = :id")
@NamedQuery(name  = "CardToStudySystem.getBoxNrForSpecificC2S",
            query = "SELECT c.box FROM CardToStudySystem c WHERE card = :card AND studySystem = :studySystem")
public class CardToStudySystem implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "card_id")
    private final Card card;

    @Getter
    @ManyToOne
    @JoinColumn(name = "study_system_id")
    private final StudySystem studySystem;

    @Getter
    @Column(name = "study_box_number")
    private int box;

    public CardToStudySystem incrementBoxNr() {
        ++box;
        return this;
    }

    public CardToStudySystem decrementBoxNr() {
        if (box > 0) {
            --box;
        }
        return this;
    }

    public CardToStudySystem moveToBoxNr(final int boxNr) {
        assert boxNr >= 0 : "Error: specified StudySystem boxNr must be >= 0";
        this.box = boxNr;
        return this;
    }

    // Constructors
    public CardToStudySystem(final Card card, final StudySystem studySystem, final int boxNr) {
        this.card = card;
        this.studySystem = studySystem;
        this.box = boxNr;
    }

    public CardToStudySystem(Card card, StudySystem studySystem, StudySystem studySystem1) {
        this(card, studySystem, 0);
    }

    public CardToStudySystem() {
        this(null, null, 0);
    }
}
