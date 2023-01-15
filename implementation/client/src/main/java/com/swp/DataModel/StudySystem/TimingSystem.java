package com.swp.DataModel.StudySystem;

import java.util.Timer;
import java.util.TimerTask;
import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Klasse für das TimingSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Timing")
public class TimingSystem extends StudySystem implements MouseListener
{


    static Timer timer = new Timer();
    static int seconds = 0;
    int timeLimit;
    int questionCount = 0;
    int trueAnswerCount = 0;
    int falseAnswerCount = 0;
    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>(); // getting answers for cards just example
    JLabel answer;
        /**
         * Konstruktor der Klasse TimingSystem.
         * @param deck: Das Deck für das Lernsystem
         */
    public TimingSystem(Deck deck,int timeLimit) {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.TIMING), 5);
        // Deck to card;
        this.timeLimit = timeLimit;

        //MyTimer();


        // Just for Pseudo Code
        answer = new JLabel();
        answer.setVisible(false);
        // Just for Pseudo Code

        answer.addMouseListener(this);

    }

    public void checkTimer(){
        if(seconds == timeLimit){
            NotificationGUI.addNotification("Time is done!", Notification.NotificationType.ERROR, 5);
            questionCount++;
            falseAnswerCount++;
            getNextCard(questionCount);
        }
    }

        @Override
        public void mouseClicked (MouseEvent e){
            String answer = "";
            if (answer.length() == 0) {
                NotificationGUI.addNotification("Answer can't be empty!", Notification.NotificationType.WARNING, 5);
            } else {
                if (checkAnswer(answer,questionCount)) {
                    NotificationGUI.addNotification("Answer is true!", Notification.NotificationType.INFO, 3);
                    questionCount++;
                    trueAnswerCount++;
                    getNextCard(questionCount);
                }
                else{
                    NotificationGUI.addNotification("Answer is wrong!", Notification.NotificationType.INFO, 3);
                    questionCount++;
                    falseAnswerCount++;
                    if(questionCount == cards.size()){
                        // Change GUI and show results
                    }
                    else {
                        getNextCard(questionCount);
                    }
                }
            }
        }

        public boolean checkAnswer (String answer,int questionCount){
            String trueAnswer = answers.get(questionCount); // getting real answer from database or server
            return trueAnswer == answer;
        }











    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

/*
    public static void MyTimer() {

        TimerTask task;

        task = new TimerTask() {
            @Override
            public void run() {
                if (seconds < timeLimit) {
                    seconds++;
                } else {
                    timer.cancel();
                }
            }
        };

    }

 */

}



