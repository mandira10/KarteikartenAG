package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Klasse für das TimingSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@Getter
@Setter
@DiscriminatorValue("Timing")
public class TimingSystem extends StudySystem
{

    /**
     * Zeitlimit für jede Frage zu beantworten
     */
    private float timeLimit = 0;
    /**
     * Tatsächliche Antwortzeit für eine Frage
     */
    private float answerTime = 0;

    /**
     * Konstruktor der Klasse TimingSystem.
     * @param name: der Name des Systems
     * @param cardOrder: CardOrdner, um die Reihenfolge der Karten festzulegen
     * @param visibility: Sichtbarkeit des Systems
     * @param timeLimit: Zeitlimit für jede Frage zu beantworten
     */
    public TimingSystem(String name, CardOrder cardOrder, boolean visibility,int timeLimit){
        super(name,cardOrder,StudySystemType.TIMING,visibility);
        this.boxes.add(new StudySystemBox(this));
        this.timeLimit = timeLimit;}

    public TimingSystem(TimingSystem other){
        super(other);
        timeLimit = other.getTimeLimit();
        answerTime = other.getAnswerTime();
    }


    public TimingSystem() {
      super("",CardOrder.ALPHABETICAL,StudySystemType.TIMING,false);
    }

}



