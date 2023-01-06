package com.swp.DataModel.StudySystem;


import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
public class LeitnerSystem extends StudySystem implements MouseListener{
    int questionCount = 0;
    int trueAnswerCount = 0;
    int falseAnswerCount = 0;
    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>(); // getting answers for cards just example
        JLabel answer;
        /**
 * Konstruktor der Klasse LeitnerSystem.
 *
 * @param deck: Das Deck für das Lernsystem
 */
public LeitnerSystem(Deck deck){
        super(deck,new StudySystemType(StudySystemType.KNOWN_TYPES.LEITNER),5);
        // Deck to card;

        // Just for Pseudo Code
        answer = new JLabel();
        answer.setVisible(false);
        // Just for Pseudo Code

        answer.addMouseListener(this);



}








        @Override
        public void mouseClicked(MouseEvent e) {
                String answer = "";
                if(answer.length()  == 0){
                        NotificationGUI.addNotification("Answer can't be empty!", Notification.NotificationType.WARNING, 5);
                }
                else{
                        if(checkAnswer(answer,questionCount)){
                                NotificationGUI.addNotification("Answer is true!", Notification.NotificationType.INFO, 3);
                                questionCount++;
                                trueAnswerCount++;
                                if(questionCount == cards.size()){
                                        // Change GUI and show results
                                }
                                else {
                                        getNextCard(questionCount);
                                }
                        }
                        else{
                                NotificationGUI.addNotification("Answer is wrong!", Notification.NotificationType.INFO, 3);
                                questionCount = 0;
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

        public boolean checkAnswer(String answer,int questionCount){
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


}

