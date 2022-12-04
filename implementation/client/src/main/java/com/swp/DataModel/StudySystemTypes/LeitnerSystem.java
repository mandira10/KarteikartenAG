package com.swp.DataModel.StudySystemTypes;

import java.util.ArrayList;
import java.util.Set;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

public class LeitnerSystem extends StudySystem 
{
    public LeitnerSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.LEITNER), 5);
        
    }
}
