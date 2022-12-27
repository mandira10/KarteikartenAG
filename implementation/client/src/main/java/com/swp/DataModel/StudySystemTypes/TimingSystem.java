package com.swp.DataModel.StudySystemTypes;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;
/**
 * Klasse für das TimingSystem. Erbt alle Attribute vom StudySystem
 */
public class TimingSystem extends StudySystem 
{
    /**
     * Konstruktor der Klasse TimingSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public TimingSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.TIMING), 5);
        /**int timeLimit; // get from User
        for(int i = 0 ; i < size of Cards that we will lernen;){
            getNextCard(); // We can use getNextCard with index
            // Countdown will start and will be refreshed when i++
            // If User cant answer in the time show to user real answer and getNextCard()
            String answer; // get answer from user
            String realAnswer; // real answer from database
            giveAnswer((realAnswer.equals(answer))); // We can send also index of card to this function
            i++;
         */


        }
        
    }

    /**
    @Override
    public void giveAnswer(boolean answer){
        if(answer){
            Show user that answer was True
            Delete Card from deck
            Update Cards priority in Datebase
        }
        else{
        Show user that answer was False
        Update Cards priority in Datebase
        }
     */

