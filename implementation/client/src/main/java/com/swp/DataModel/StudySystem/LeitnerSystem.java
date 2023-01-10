package com.swp.DataModel.StudySystem;


import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.DeckLogic;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
public class LeitnerSystem extends StudySystem {
        private int questionCount = 0;
        private int trueAnswerCount = 0;
        private int pointQuestion = 100;
        private int resultPoint = 0;

        /**
         * Konstruktor der Klasse LeitnerSystem.
         *
         * @param deck: Das Deck für das Lernsystem
         */
        public LeitnerSystem(Deck deck) {
                super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.LEITNER), 5);
        }

        @Override
        public void giveAnswer(boolean answer) {
                if(answer){
                        trueAnswerCount++;
                        questionCount++;
                }
                else{
                        questionCount = 0;
                }
        }

        @Override
        public Card getNextCard(int index) {
                return getAllCardsInStudySystem().get(questionCount);
        }

        @Override
        public void finishTest() {
                if(questionCount++ == getAllCardsInStudySystem().size()) {
                        if (trueAnswerCount == 0) {
                                resultPoint = 0;
                        } else {
                                pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
                                resultPoint = pointQuestion * trueAnswerCount;
                        }
                }
        }

        @Override
        public int getResult() {
                return resultPoint;
        }

        @Override
        public float getProgress() {
                if(getAllCardsInStudySystem().size() == 0){
                        return 0;
                }
                else{
                        return trueAnswerCount / getAllCardsInStudySystem().size();
                }

        }
}
