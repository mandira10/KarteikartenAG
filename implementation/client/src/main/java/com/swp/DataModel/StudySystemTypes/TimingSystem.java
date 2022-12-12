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
        
    }
}