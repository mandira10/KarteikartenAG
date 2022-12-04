package com.swp.Persistence;

import java.util.HashSet;
import java.util.Set;

import com.swp.DataModel.*;

public class Cache 
{
    private static Cache pCacheInstance = null;
    private Set<Deck> stDecks;
    private Set<Category> stCategories;
    private Set<Tag> stTags;
    private Set<CardToDeck> stCardToDecks;
    private Set<CardToCategory> stCardToCategories;
    private Set<CardToTag> stCardToTags;

    private Cache()
    {
        stDecks = new HashSet<>();
        stCategories = new HashSet<>();
        stTags = new HashSet<>();
        stCardToDecks = new HashSet<>();
        stCardToCategories = new HashSet<>();
        stCardToTags = new HashSet<>();
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
    public void setDecks(Set<Deck> stDecks)                                 { this.stDecks = stDecks; }
    public void setCategories(Set<Category> stCategories)                   { this.stCategories = stCategories; }
    public void setTags(Set<Tag> stTags)                                    { this.stTags = stTags; }
    public void setCardToDecks(Set<CardToDeck> stCardToDecks)               { this.stCardToDecks = stCardToDecks; }
    public void setCardToCategories(Set<CardToCategory> stCardToCategories) { this.stCardToCategories = stCardToCategories; }
    public void setCardToTags(Set<CardToTag> stCardToTags)                  { this.stCardToTags = stCardToTags; }

    //
    // Getter
    //
    public Set<Tag> getTags()                        { return stTags; }
    public Set<Deck> getDecks()                      { return stDecks; }
    public Set<Category> getCategories()             { return stCategories; }
    public Set<CardToTag> getCardToTags()            { return stCardToTags; }
    public Set<CardToDeck> getCardToDecks()          { return stCardToDecks; }
    public Set<CardToCategory> getCardToCategories() { return stCardToCategories; }



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