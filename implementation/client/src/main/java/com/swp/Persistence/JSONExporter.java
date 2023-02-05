package com.swp.Persistence;

import com.gumse.tools.Output;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Exportiert eine liste von Karten als JSON Datei
 * @author Tom Beuke
 */
public class JSONExporter
{
    private JSONExporter()
    {

    }


    /**
     * Gibt Kategorien einer Karte als JSONArray wieder
     *
     * @param card Die Karte
     * @return Die Kategorien der Karte als JSONArray
     */
    public static synchronized JSONArray getCardCategories(Card card)
    {
        JSONArray retArray = new JSONArray();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
        CategoryController.getInstance().getCategoriesToCard(card, new DataCallback<>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                for(Category category : categories)
                    retArray.put(category.getName());
            }

            @Override public void onFailure(String msg) {
                Output.error("Failed to get categories for card " + card.getTitle() + ": " + msg);
            }            
        });
        ControllerThreadPool.getInstance().synchronizedTasks(false);

        return retArray;
    }

    /**
     * Gibt Tags einer Karte als JSONArray wieder
     *
     * @param card Die Karte
     * @return Die Tags der Karte als JSONArray
     */
    public static synchronized JSONArray getCardTags(Card card)
    {
        JSONArray retArray = new JSONArray();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
        CardController.getInstance().getTagsToCard(card, new DataCallback<>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Tag> tags) 
            {
                for(Tag tag : tags)
                    retArray.put(tag.getVal());
            }

            @Override public void onFailure(String msg) {
                Output.error("Failed to get tags for card " + card.getTitle() + ": " + msg);
            }            
        });
        ControllerThreadPool.getInstance().synchronizedTasks(false);

        return retArray;
    }

    /**
     * Fügt Kartenspezifische Daten dem JSONObject hinzu
     *
     * @param card    Die Karte
     * @param jsonobj Das Eltern JSONObject
     */
    public static void putCardSpecificData(Card card, JSONObject jsonobj)
    {
        switch(card.getType())
        {
            case AUDIO:
                AudioCard acard = (AudioCard)card;
                jsonobj.put("answer", acard.getAnswer());
                jsonobj.put("audio", acard.getAudio());
                break;

            case IMAGEDESC:
                ImageDescriptionCard icard = (ImageDescriptionCard)card;
                JSONArray ianswersarray = new JSONArray();
                for(ImageDescriptionCardAnswer answer : icard.getAnswers())
                {
                    JSONObject answerobj = new JSONObject();
                    answerobj.put("answer", answer.answertext);
                    answerobj.put("x", answer.xpos);
                    answerobj.put("y", answer.ypos);
                    ianswersarray.put(answerobj);
                }
                jsonobj.put("answers", ianswersarray);
                jsonobj.put("image", icard.getImage());
                break;

            case IMAGETEST:
                ImageTestCard itcard = (ImageTestCard)card;
                jsonobj.put("answer", itcard.getAnswer());
                jsonobj.put("image", itcard.getImage());
                break;

            case MULITPLECHOICE:
                MultipleChoiceCard mcard = (MultipleChoiceCard)card;
                JSONArray manswersarray = new JSONArray();
                List<Integer> correctAnswers = Arrays.stream(mcard.getCorrectAnswers()).boxed().toList();
                for(int i = 0; i < mcard.getAnswers().length; i++)
                {
                    JSONObject answerobj = new JSONObject();
                    answerobj.put("answer", mcard.getAnswers()[i]);
                    answerobj.put("correct", correctAnswers.contains(i));
                    manswersarray.put(answerobj);
                }
                jsonobj.put("answers", manswersarray);
                break;

            case TEXT:
                TextCard tcard = (TextCard)card;
                jsonobj.put("answer", tcard.getAnswer());
                break;

            case TRUEFALSE:
                TrueFalseCard tfcard = (TrueFalseCard)card;
                jsonobj.put("answer", tfcard.isAnswer());
                break;
            default:
                break;
            
        }
    }


    /**
     * Exportiert die Karten in eine Ausgabedatei
     *
     * @param cards       Die Karten, welche exportiert werden sollen
     * @param destination Die Ausgabedatei
     * @return Gibt true bei erfolgreichem Exportieren wieder
     */
    public static boolean export(List<Card> cards, String destination)
    {
        Writer output;
        try { output = new FileWriter(destination); } 
        catch (IOException e) 
        {
            Output.error("Failed to open file " + destination + ": " + e.getMessage());
            return false;
        }
        
        JSONArray cardsArray = new JSONArray();
        for(Card card : cards)
        {
            JSONObject jsonobj = new JSONObject();
            jsonobj.put("type", card.getType());
            jsonobj.put("title", card.getTitle());
            jsonobj.put("question", card.getQuestion());
            jsonobj.put("rating", card.getRating());
            jsonobj.put("uuid", card.getUuid());
            jsonobj.put("creationdate", card.getCreationDate());
            jsonobj.put("references", new JSONArray(card.getReferences().split("\n")));
            jsonobj.put("categories", getCardCategories(card));
            jsonobj.put("tags", getCardTags(card));
            putCardSpecificData(card, jsonobj);

            cardsArray.put(jsonobj);
        }

        JSONObject cardsObject = new JSONObject();
        cardsObject.put("cards", cardsArray);

        try 
        {
            output.write(cardsObject.toString(2));
            output.close();
        } 
        catch (JSONException | IOException e) 
        {
            Output.error("Failed to write file " + destination + ": " + e.getMessage());
            return false;
        }


        return true;
    }
}