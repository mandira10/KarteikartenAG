package com.swp.Persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.StudySystem;

public class Cache 
{
    private static Cache pCacheInstance = null;
    //private List<Deck> alDecks;
    private Set<StudySystem.StudySystemType> stStudySystemTypes;
    private Set<Category> stCategories;
    private Set<Tag> stTags;
    //private List<CardToDeck> alCardToDecks;
    private Set<CardToCategory> stCardToCategories;
    private Set<CardToTag> stCardToTags;

    private Cache()
    {
        //alDecks = new ArrayList<>();
        stCategories = new HashSet<>();
        stTags = new HashSet<>();
       // alCardToDecks = new ArrayList<>();
        stCardToCategories = new HashSet<>();
        stCardToTags = new HashSet<>();
        stStudySystemTypes = new HashSet<>();
    }

    public static Cache getInstance()
    {
        if(pCacheInstance == null)
            pCacheInstance = new Cache();

        return pCacheInstance;
    }

    
    //
    // Setter
    //
    //public void setDecks(List<Deck> decks)                                  { this.alDecks = decks; }
    public void setCategories(Set<Category> stCategories)                   { this.stCategories = stCategories; }
    public void setTags(Set<Tag> stTags)                                    { this.stTags = stTags; }
    //public void setCardToDecks(List<CardToDeck> cardToDecks)                { this.alCardToDecks = cardToDecks; }
    public void setCardToCategories(Set<CardToCategory> stCardToCategories) { this.stCardToCategories = stCardToCategories; }
    public void setCardToTags(Set<CardToTag> stCardToTags)                  { this.stCardToTags = stCardToTags; }
    public void setStudySystemTypes(Set<StudySystem.StudySystemType> stStudySystemTypes){ this.stStudySystemTypes = stStudySystemTypes; }


    //
    // Getter
    //
    public Set<Tag> getTags()                        { return stTags; }
   // public List<Deck> getDecks()                     { return alDecks; }
    public Set<Category> getCategories()             { return stCategories; }
    public Set<CardToTag> getCardToTags()            { return stCardToTags; }
   // public List<CardToDeck> getCardToDecks()         { return alCardToDecks; }
    public Set<CardToCategory> getCardToCategories() { return stCardToCategories; }
    public Set<StudySystem.StudySystemType> getStudySystemTypes(){ return stStudySystemTypes; }



    /*public Deck getDeck(String uuid)
    {
        for(Deck d : stDecks)
        {
            if(d.getUUID() == uuid)
                return d;
        }

        return null;
    }

    public Category getCategory(String uuid)
    {
        for(Category c : stCategories)
        {
            if(c.getUUID() == uuid)
                return c;
        }

        return null;
    }

    public Tag getTag(String uuid)
    {
        for(Tag t : stTags)
        {
            if(t.getUUID() == uuid)
                return t;
        }

        return null;
    }*/
}