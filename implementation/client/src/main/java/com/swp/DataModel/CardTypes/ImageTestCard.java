package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;


/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageTestCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class ImageTestCard extends Card 
{
    /**
     * Frage für das Bild
     */
    private String sQuestion;

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
        setTitle("ImageTestCard");
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
        setTitle(title);
        sQuestion = question;
        sAnswer = answer;
        oImage = image;
        bSwapQA = swapQA;
        bVisibility = visible;
        sContent = getContent();
    }


    public String getQuestion() {
        return sQuestion;
    }

    public Texture getImage() {
        return oImage;
    }

    public void setQuestion(String sQuestion) {
        this.sQuestion = sQuestion;
    }

    public String getAnswer() {return sAnswer;}



    public boolean isSwapQA() {
        return bSwapQA;
    }

    public void setAnswer(Texture oAnswer) {
        this.oImage = oAnswer;
    }

    public void setSwapQA(boolean bSwapQA) {
        this.bSwapQA = bSwapQA;
    }

    public void setAnswer(String sAnswer) {this.sAnswer = sAnswer;}

    @Override
    public String getContent(){
        StringBuilder string = new StringBuilder();
        return string.append(sTitle+" "+sQuestion+" "+sAnswer).toString();
    }
}
