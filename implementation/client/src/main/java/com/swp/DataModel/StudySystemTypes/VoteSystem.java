package com.swp.DataModel.StudySystemTypes;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

public class VoteSystem extends StudySystem 
{
    public VoteSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.VOTE), 5);
        
    }
}