package com.swp.DataModel.StudySystemTypes;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

public class TimingSystem extends StudySystem 
{
    public TimingSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.TIMING), 5);
        
    }
}