package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Klasse für das VoteSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Vote")
public class VoteSystem extends StudySystem implements MouseListener {
    int questionCount = 0;
    int trueAnswerCount = 0;
    int falseAnswerCount = 0;
    ArrayList<Card> cards = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>(); // getting answers for cards just example
    HashMap<String, Integer> unsortedCards = new HashMap<>();
    LinkedHashMap<String, Integer> sortedCards = new LinkedHashMap<>();
    ArrayList<Integer> list = new ArrayList<>();
    JLabel answer;
    /**
     * Konstruktor der Klasse VoteSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public VoteSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.VOTE), 5);
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
                int vote = 5; // will get from gui
                unsortedCards.put(cards.get(questionCount).getQuestion(),vote);
                questionCount++;
                trueAnswerCount++;
                if(questionCount == cards.size()){
                    for (Map.Entry<String, Integer> entry : unsortedCards.entrySet()) {
                        list.add(entry.getValue());
                    }
                    Collections.sort(list);
                    for (int num : list) {
                        for (Map.Entry<String, Integer> entry : unsortedCards.entrySet()) {
                            if (entry.getValue().equals(num)) {
                                sortedCards.put(entry.getKey(), num);
                            }
                        }
                    }
                    // Update Deck nach sortedCards
                    // Change GUI and show results
                }
                else {
                    getNextCard(questionCount);
                }
            }
            else{
                NotificationGUI.addNotification("Answer is wrong!", Notification.NotificationType.INFO, 3);
                int vote = 5; // will get from gui
                unsortedCards.put(cards.get(questionCount).getQuestion(),vote);
                questionCount++;
                falseAnswerCount++;
                if(questionCount == cards.size()){
                    for (Map.Entry<String, Integer> entry : unsortedCards.entrySet()) {
                        list.add(entry.getValue());
                    }
                    Collections.sort(list);
                    for (int num : list) {
                        for (Map.Entry<String, Integer> entry : unsortedCards.entrySet()) {
                            if (entry.getValue().equals(num)) {
                                sortedCards.put(entry.getKey(), num);
                            }
                        }
                    }
                    // Update Deck nach sortedCards
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