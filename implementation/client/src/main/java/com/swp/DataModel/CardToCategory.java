package com.swp.DataModel;

public class CardToCategory 
{
    private Card pCard;
    private Category pCategory;

    public CardToCategory(Card c, Category d)
    {
        this.pCard = c;
        this.pCategory = d;
    }


    //
    // Getter
    //
    public Card getCard()         { return pCard; }
    public Category getCategory() { return pCategory; }
}