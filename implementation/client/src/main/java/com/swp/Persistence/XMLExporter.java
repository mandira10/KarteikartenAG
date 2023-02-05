package com.swp.Persistence;

import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.gumse.tools.Output;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;

/**
 * Exportiert eine liste von Karten als XML Datei
 */
public class XMLExporter
{
    private XMLExporter()
    {

    }

    /**
     * Fügt Kategorien einer Karte dem XML Element hinzu
     *
     * @param parent  Das Elternelement
     * @param doc     Das XML Dokument
     * @param card    Die Karte
     */
    public static synchronized void addCardCategories(Element parent, Document doc, Card card)
    {
        Element categoryElement = doc.createElement("categories");
        ControllerThreadPool.getInstance().synchronizedTasks(true);
        CategoryController.getInstance().getCategoriesToCard(card, new DataCallback<>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Category> categories) 
            {
                for(Category category : categories)
                    addElement(categoryElement, doc, "category", category.getName());
            }

            @Override public void onFailure(String msg) {
                Output.error("Failed to get categories for card " + card.getTitle() + ": " + msg);
            }            
        });
        ControllerThreadPool.getInstance().synchronizedTasks(false);
        parent.appendChild(categoryElement);
    }


    /**
     * Fügt Tags einer Karte dem XML Element hinzu
     *
     * @param parent  Das Elternelement
     * @param doc     Das XML Dokument
     * @param card    Die Karte
     */
    public static synchronized void addCardTags(Element parent, Document doc, Card card)
    {
        Element tagsElement = doc.createElement("tags");
        ControllerThreadPool.getInstance().synchronizedTasks(true);
        CardController.getInstance().getTagsToCard(card, new DataCallback<>() {
            @Override public void onInfo(String msg) {}
            @Override public void onSuccess(List<Tag> tags) 
            {
                for(Tag tag : tags)
                    addElement(tagsElement, doc, "tag", tag.getVal());
            }

            @Override public void onFailure(String msg) {
                Output.error("Failed to get tags for card " + card.getTitle() + ": " + msg);
            }            
        });
        ControllerThreadPool.getInstance().synchronizedTasks(false);
        parent.appendChild(tagsElement);
    }


    /**
     * Fügt Kartenspezifische Daten dem XML Element hinzu
     *
     * @param parent  Das Elternelement
     * @param doc     Das XML Dokument
     * @param card    Die Karte
     */
    public static void putCardSpecificData(Element parent, Document doc, Card card)
    {
        switch(card.getType())
        {
            case AUDIO:
                AudioCard acard = (AudioCard)card;
                addElement(parent, doc, "answer", acard.getAnswer());
                //addElement(parent, doc, "audio", acard.getAudio().toString());
                break;

            case IMAGEDESC:
                ImageDescriptionCard icard = (ImageDescriptionCard)card;
                Element answersElement = doc.createElement("answers");
                for(ImageDescriptionCardAnswer answer : icard.getAnswers())
                {
                    Element answerElem = doc.createElement("answer");
                    answerElem.setTextContent(answer.answertext);
                    answerElem.setAttribute("x", String.valueOf(answer.xpos));
                    answerElem.setAttribute("y", String.valueOf(answer.ypos));
                    answersElement.appendChild(answerElem);
                }
                //addElement(parent, doc, "image", icard.getImage());
                parent.appendChild(answersElement);
                break;

            case IMAGETEST:
                ImageTestCard itcard = (ImageTestCard)card;
                addElement(parent, doc, "answer", itcard.getAnswer());
                //addElement(parent, doc, "image", itcard.getImage().toString());
                break;

            case MULITPLECHOICE:
                MultipleChoiceCard mcard = (MultipleChoiceCard)card;
                Element manswersElement = doc.createElement("answers");
                List<Integer> correctAnswers = Arrays.stream(mcard.getCorrectAnswers()).boxed().toList();
                for(int i = 0; i < mcard.getAnswers().length; i++)
                {
                    Element answerElem = doc.createElement("answer");
                    answerElem.setTextContent(mcard.getAnswers()[i]);
                    answerElem.setAttribute("correct", correctAnswers.contains(i) ? "true" : "false");
                    manswersElement.appendChild(answerElem);
                }
                parent.appendChild(manswersElement);
                break;

            case TEXT:
                TextCard tcard = (TextCard)card;
                addElement(parent, doc, "answer", tcard.getAnswer());
                break;

            case TRUEFALSE:
                TrueFalseCard tfcard = (TrueFalseCard)card;
                addElement(parent, doc, "answer", tfcard.isAnswer() ? "true" : "false");
                break;
            default:
                break;
            
        }
    }

    private static void addElement(Element parent, Document doc, String name, String value)
    {
        Element child = doc.createElement(name);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    private static void addReferences(Element parent, Document doc, Card card)
    {
        Element child = doc.createElement("references");
        for(String reference : card.getReferences().split("\n"))
            addElement(child, doc, "ref", reference);
        parent.appendChild(child);
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
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try { docBuilder = docFactory.newDocumentBuilder(); } 
        catch (ParserConfigurationException e) 
        {
            Output.error("Failed to create new document builder: " + e.getMessage());
            return false;
        }

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("cards");
        doc.appendChild(rootElement);

        for(Card card : cards)
        {
            Element cardelem = doc.createElement("card");
            addElement(cardelem, doc, "type", card.getType().toString());
            addElement(cardelem, doc, "title", card.getTitle());
            addElement(cardelem, doc, "question", card.getQuestion());
            addElement(cardelem, doc, "rating", String.valueOf(card.getRating()));
            addElement(cardelem, doc, "uuid", card.getUuid());
            addElement(cardelem, doc, "creationdate", card.getCreationDate().toString());
            addReferences(cardelem, doc, card);

            rootElement.appendChild(cardelem);

            addCardCategories(cardelem, doc, card);
            addCardTags(cardelem, doc, card);
            putCardSpecificData(cardelem, doc, card);
        }

        try 
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(destination);
            transformer.transform(source, result);
        } 
        catch (TransformerException e) 
        {
            Output.error("Failed to save XML file " + destination + ": " + e.getMessage());
            return false;
        }




        return true;
    }
}
