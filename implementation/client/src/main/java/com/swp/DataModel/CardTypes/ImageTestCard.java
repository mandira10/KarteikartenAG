package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import lombok.Getter;
import lombok.Setter;


/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageTestCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
public class ImageTestCard extends Card 
{

    /**
     * Bild für die Karteikarte
     */
    private Texture oImage;

    /**
     * Zusätzliche Beschreibung zum Bild als Antwort
     */
    private String sAnswer;

    /**
     * Tauscht die Frage/Antwort Option, sodass das Bild sowohl
     * als Frage als auch als Antwort verwendet werden kann.
     */
    private boolean bSwapQA;

    /**
     * Leerer Konstruktor der Klasse ImageTestCard
     */
    public ImageTestCard()
    {
        super(CardType.IMAGETEST);
        setSTitle("ImageTestCard");
    }

    /**
     * Konstruktor der Klasse ImageTestCard
     * @param question: Textuelle Beschreibung
     * @param answer: Text zum Bild
     * @param image: Bild zur Antwort
     * @param swapQA: Wechsel Frage/Antwort
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageTestCard(String question, String answer, Texture image, String title, boolean swapQA, boolean visible)
    {
        super(CardType.IMAGETEST);
        setSTitle(title);
        sQuestion = question;
        sAnswer = answer;
        oImage = image;
        bSwapQA = swapQA;
        bVisibility = visible;
        sContent = getSContent();
    }



    @Override
    public String getSContent(){
        return sTitle + "\n" + sQuestion + "\n" + sAnswer;
    }
}
