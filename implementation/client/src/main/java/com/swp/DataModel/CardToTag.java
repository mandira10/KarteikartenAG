package com.swp.DataModel;

public class CardToTag 
{
    private Card pCard;
    private Tag pTag;

    public CardToTag(Card c, Tag d)
    {
        this.pCard = c;
        this.pTag = d;
    }


    //
    // Getter
    //
    public Card getCard() { return pCard; }
    public Tag getTag()   { return pTag; }
}