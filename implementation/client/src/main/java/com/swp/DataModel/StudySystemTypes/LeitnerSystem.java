package com.swp.DataModel.StudySystemTypes;


import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
public class LeitnerSystem extends StudySystem 
{
    /**
     * Konstruktor der Klasse LeitnerSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public LeitnerSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.LEITNER), 5);
        /**
         for(int i = 0 ; i < size of cards that we will lernen;){
             getNextCard(); // We can use getNextCard with index
             String answer; // get answer from user
             String realAnswer; // real answer from database
             giveAnswer((realAnswer.equals(answer))); // We can send also index of card to this function
             if(answer.equals(realAnswer)){
                i++;
            }
            else{
                if(i == 0){
                    i == 0;
                }
                else{
                    i--;
                }
            }
         }


         */
        
    }

    /**
     @Override
     public void giveAnswer(boolean answer){
         if(answer){
             Show user that answer was True
             Update Cards priority in Datebase
         }
         else{
             Show user that answer was False
             Update Cards priority in Datebase
         }
     */
}
