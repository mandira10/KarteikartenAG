package com.swp.Controller;


import com.swp.DataModel.*;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class CardController
{

    private CardController() {}

    public static Set<Card> getCardsByTag(Tag tag)                           {
       return CardLogic.getCardsByTag(tag);
       //TODO : Exceptions auffangen und einbauen. Welche Fehlermeldungen Optionen gibt es im GUI??
    }

    public static Set<Card> getCardsBySearchterms(String searchterms)        { return CardLogic.getCardsBySearchterms(searchterms); }
    public static Card getCardByUUID(String uuid)                            { return CardLogic.getCardByUUID(uuid); }
    public static boolean deleteCard(Card card)                              { return CardLogic.deleteCard(card); }
    public static boolean deleteCards(Card[] card)                           { return CardLogic.deleteCards(card); }
    public static boolean createTag(String value)                            { return CardLogic.createTag(value); }
    public static Set<Tag> getTags()                                         { return CardLogic.getTags(); }
    public static boolean updateCardData(Card oldcard, Card newcard)         { return CardLogic.updateCardData(oldcard, newcard); }

    public static boolean createCardToTag(Card card, Tag tag)                 { return CardLogic.createCardToTag(card, tag); }
    public static Set<Card> getCardsToShow(long begin, long end)             { return CardLogic.getCardsToShow(begin, end); }
    public static boolean exportCards(Card[] cards, ExportFileType filetype) { return CardLogic.exportCards(cards, filetype); }
}
