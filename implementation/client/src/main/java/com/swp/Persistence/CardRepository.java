package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardRepository
{
    private static final PersistenceManager pm = new PersistenceManager();
    public static Set<Card> getCards(long from, long to)
    {
        //////////////////////////////////////
        // For Testing 
        //////////////////////////////////////
        Set<Card> cards = new HashSet<>();
        Texture ketTexture = new Texture("ket");
        ketTexture.load("textures/orange-ket.png", CardRepository.class);
        for(int i = 0; i < to - from; i += 6)
        {
            cards.add(new AudioCard(null, "AudioCardTitle", "Some Audio Related Question", "The Correct Audio Answer", false, true));
            cards.add(new ImageDescriptionCard("Some Image Description Question", "Correct Image Description Answer", "ImageDescriptionTitle", ketTexture, false));
            cards.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", ketTexture, "ImageTestCardTitle", false, true));
            cards.add(new MultipleChoiceCard("MultipleChoice Question", new String[]{"Correct Answer1", "Answer2", "Answer3", "Correct Answer4"}, new int[]{0, 3}, "MultipleChoiceCardTitle", false));
            cards.add(new TextCard("Some Text Question", "Correct Text Answer", "TextCardTitle", false));
            cards.add(new TrueFalseCard("TrueFalse Question", true, "TrueFalseCardTitle", false));
        }
        /*
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.createQuery();
            em.getTransaction().commit();
        }
        */
        return cards;
    }

    public static Set<Card> findCardsByCategory(Category category)
    {
        return null;
    }

    public static Set<Card> findCardsByTag(Tag tag)
    {
        return null;
    }

    public static Set<Card> findCardsContaining(String searchWords)
    {
        return null;
    }

    public static Card getCardByUUID(String uuid)
    {
        return null;
    }

    /**
     *
     * @param oldcard
     * @param newcard
     */
    public static boolean updateCard(Card oldcard, Card newcard)
    {
        String jsonString = "";
        if(!oldcard.getTitle().equals(newcard.getTitle()))
            jsonString += "\"title\":" + newcard.getTitle();

        switch(newcard.getType())
        {
            case AUDIO:
                break;
            case IMAGEDESC:
                break;
            case IMAGETEST:
                break;
            case MULITPLECHOICE:
                break;
            case TEXT:
                break;
            case TRUEFALSE:
                break;
            default:
                Debug.error("Unknown cardtype!");
                break;

        }

        //return server.send("/updatecarddata", jsonString);
        return false;
    }

    public static boolean saveCard(Card card)
    {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.persist(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean deleteCard(Card card)
    {
        try (final EntityManager em = pm.getEntityManager()) {
            em.getTransaction().begin();
            em.remove(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            return false;
        }
        return true;
    }




    //
    // Tags
    //
    public static boolean saveTag(String value)
    {
        //return server.send("/createtag", jsonString);
        return false;
    }

    public static Set<Tag> getTags()
    {
        Set<Tag> tags = Cache.getInstance().getTags();
        if(!tags.isEmpty())
            return tags;

        //server.send("/gettags", jsonString);
        return null;
    }

    public static Set<CardToTag> getCardToTags()
    {
        Set<CardToTag> tags = Cache.getInstance().getCardToTags();
        if(!tags.isEmpty())
            return tags;

        //server.send("/getcardtotags", jsonString);
        return null;
    }
}
