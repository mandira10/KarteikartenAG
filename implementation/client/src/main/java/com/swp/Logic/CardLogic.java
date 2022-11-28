package com.swp.Logic;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;

import javax.sound.sampled.AudioFileFormat;
import java.util.List;

public class CardLogic
{
    CardRepository cardRepository;

    public List<Card> getCardsForCategory(String category)
	{
        Category category1 = new Category();
        return cardRepository.findCardsByCategory(category1);
    }

    public List<Card> getCardsForTag(String tag)
	{
        Tag tag1 = new Tag();
        return cardRepository.findCardsByTag(tag1);

    }

    public List<Card> getCardsForSearchWords(String searchWords)
	{
        return cardRepository.findCardsWith(searchWords);
    }

    public TrueFalseCard createTrueFalseCard(String question, boolean answer, boolean visibility) 
	{
        TrueFalseCard retCard = new TrueFalseCard();
        cardRepository.saveCard(retCard);
        
        return retCard;
    }

    public ImageTestCard createImageTestCard(String question, Texture answer, boolean qSwapQA, boolean visibility) {
        ImageTestCard retCard = new ImageTestCard();
        cardRepository.saveCard(retCard);

        return retCard;
    }

    public AudioCard createAudioCard(AudioFileFormat question, String answer, boolean qSwapQA, boolean visibility) {
        AudioCard retCard = new AudioCard();
        cardRepository.saveCard(retCard);

        return retCard;

    }

    public ImageDescriptionCard createImageDescCard(Texture question, String answer, boolean visibility) {
        ImageDescriptionCard retCard = new ImageDescriptionCard();
        cardRepository.saveCard(retCard);

        return retCard;

    }

    public TextCard createTextCard(String question, String answer, boolean visibility) {
        TextCard retCard = new TextCard();
        cardRepository.saveCard(retCard);

        return retCard;
    }

    public MultipleChoiceCard createMultipleChoiceCard(String question, String[] answers, int[] isCorrectAnswers, boolean visibility) {
        MultipleChoiceCard retCard = new MultipleChoiceCard();
        cardRepository.saveCard(retCard);

        return retCard;
    }

    public Card getAllInfosForCard(String card)
	{
        return cardRepository.findCardByName(card);
    }

    public int getCountOfDecksFor(String card)
	{
        Card specificCard = cardRepository.findCardByName(card);
        return cardRepository.findNumberOfDecksToCard(specificCard);
    }

    public List<Card> getCardsToShow(long begin, long end){
        return cardRepository.getCards(begin, end);
    }

    public void createCardToDeck(Card card, Deck deck) {
    }

    public void createCardToDeckForCategory(Category category, Deck deck) {
    }

    public void createCardToCategory(Card card, Category category) {
    }




}
