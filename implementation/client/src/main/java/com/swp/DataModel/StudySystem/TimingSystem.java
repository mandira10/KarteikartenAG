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

    //TODO MERT JAVA DOC
    private float timeLimit = 0;
    private float answerTime = 0;
    private int trueCount = 0;

    /**
     * Konstruktor der Klasse TimingSystem.
     * TODO
     */
    public TimingSystem(String name, CardOrder cardOrder, boolean visibility,int timeLimit){
        super(name,cardOrder,StudySystemType.TIMING,visibility);
        this.boxes.add(new StudySystemBox(this));
//        super(StudySystemType.TIMING, 5);
             this.timeLimit = timeLimit;}

    public TimingSystem(TimingSystem other){
        super(other);
        timeLimit = other.getTimeLimit();
        answerTime = other.getAnswerTime();
        trueCount = other.getTrueCount();

    }


    public TimingSystem() {
        //this(null,5);
    }

//    @Override
//    public void giveAnswer(boolean answer) {
//        if(answer && (answerTime <= timeLimit)){
//            trueCount++;
//        }
//    }
//
//    @Override
//    public void giveTime(float seconds) {
//        answerTime = seconds;
//    }
//
//    @Override
//    public void finishTest() {
//        if(questionCount++ == getAllCardsInStudySystem().size()) {
//            if (trueCount == 0) {
//                resultPoint = 0;
//            } else {
//                pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
//                resultPoint = pointQuestion * trueCount;
//            }
//        }
//    }
//
//    @Override
//    public int getResult() {
//        return resultPoint;
//    }
//
//    @Override
//    public Card getNextCard(int index) {
//        if(questionCount++ == getAllCardsInStudySystem().size()){
//            return null;
//        }
//        else{
//            questionCount++;
//            answerTime = 0;
//            return getAllCardsInStudySystem().stream().toList().get(questionCount);
//        }
//
//    }
//
//    @Override
//    public float getProgress() {
//        if(getAllCardsInStudySystem().size() == 0){
//            return 0;
//        }
//        else{
//            return trueCount / getAllCardsInStudySystem().size();
//        }
//    }
}



