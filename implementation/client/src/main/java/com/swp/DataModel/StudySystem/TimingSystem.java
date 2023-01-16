package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Klasse für das TimingSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Timing")
public class TimingSystem extends StudySystem implements MouseListener
{
    private float timeLimit = 0;
    private float answerTime = 0;
    private int questionCount = 0;
    private int trueCount = 0;
    private int pointQuestion = 100;
    private int resultPoint = 0;


    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>(); // getting answers for cards just example
    JLabel answer;

    /**
     * Konstruktor der Klasse TimingSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public TimingSystem(Deck deck,int timeLimit) {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.TIMING), 5);
        this.timeLimit = timeLimit;

    }

    @Override
    public void giveAnswer(boolean answer) {
        if(answer && (answerTime <= timeLimit)){
            trueCount++;
        }
    }

    @Override
    public void giveTime(float seconds) {
        answerTime = seconds;
    }

    @Override
    public void finishTest() {
        if(questionCount++ == getAllCardsInStudySystem().size()) {
            if (trueCount == 0) {
                resultPoint = 0;
            } else {
                pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
                resultPoint = pointQuestion * trueCount;
            }
        }
    }

    @Override
    public int getResult() {
        return resultPoint;
    }

    @Override
    public Card getNextCard(int index) {
        if(questionCount++ == getAllCardsInStudySystem().size()){
            return null;
        }
        else{
            questionCount++;
            answerTime = 0;
            return getAllCardsInStudySystem().get(questionCount);
        }

    }

    @Override
    public float getProgress() {
        if(getAllCardsInStudySystem().size() == 0){
            return 0;
        }
        else{
            return trueCount / getAllCardsInStudySystem().size();
        }
    }
}



