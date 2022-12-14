package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class ImageDescriptionCard extends Card
{
    /**
     * Bilddatei für die Frage
     */
    private Texture oImage;

    /**
     * Antwort der Frage
     */
    private String sAnswer;

    /**
     * Frage, die zusätzlich zum Bildelement verwendet wird
     */
    private String sQuestion;

    /**
     * Leerer Konstruktor der Klasse ImageDescriptionCard
     */
    public ImageDescriptionCard()
    {
        super(CardType.IMAGEDESC);
        setTitle("ImageDescriptionCard");
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param question Textuelle Frage zum Bild
     * @param answer: Antwort
     * @param image: Bild für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageDescriptionCard(String question, String answer, String title, Texture image, boolean visible)
    {
        super(CardType.IMAGEDESC);
        setTitle(title);
        sQuestion = question;
        oImage = image;
        sAnswer = answer;
        bVisibility = visible;
    }

    public Texture getImage() {
        return oImage;
    }

    public String getAnswer() {
        return sAnswer;
    }

    public String getQuestion() {return sQuestion;}


    public void setAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }

    public void setImage(Texture oImage) {
        this.oImage = oImage;
    }

    public void setsQuestion(String sQuestion) {this.sQuestion = sQuestion;}
}