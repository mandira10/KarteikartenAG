package com.swp.DataModel.StudySystemTypes;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

/**
 * Klasse für das VoteSystem. Erbt alle Attribute vom StudySystem
 */
public class VoteSystem extends StudySystem 
{
    /**
     * Konstruktor der Klasse VoteSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public VoteSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.VOTE), 5);
        
    }
}