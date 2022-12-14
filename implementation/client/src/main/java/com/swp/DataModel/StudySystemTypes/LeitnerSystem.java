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
        
    }
}
