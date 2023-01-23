package com.swp;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;

import java.util.ArrayList;
import java.util.List;

/**
 * Kann für Tests verwendet werden.
 */
public class TestData {

     static CategoryLogic categoryLogic = CategoryLogic.getInstance();
     static CardLogic cardLogic = CardLogic.getInstance();

    public static void importTestData() {
        //Karten 1: random types
        List<Card> randomL = new ArrayList<>();
        //randomL.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[]{}, "ImageDescriptionTitle", "textures/orange-ket.png", false));
        randomL.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", "textures/orange-ket.png", "ImageTestCardTitle", false, true));
        List<Tag> tagsRandom = new ArrayList<>() {
            {
                add(new Tag("toll"));
                add(new Tag("cool"));
            }
        };

        List<Category> randomC = new ArrayList<>() {
            {
                add(new Category("random"));
            }
        };


        List<Card> erdkundeL = new ArrayList<>();
        //Karten 2: Erdkunde Fragen MC
        erdkundeL.add(new MultipleChoiceCard("Welches der folgenden ist das flächenmäßig kleinste Bundesland?", new String[]{"Thüringen", "Sachsen", "Saarland"}, new int[]{2}, "Bundesländer Größe", false));
        erdkundeL.add(new MultipleChoiceCard("Welcher See liegt nicht in Bayern?", new String[]{"Ammersee", "Schweriner See", "Starnberger See"}, new int[]{0}, "Sees in Bayern", false));
        erdkundeL.add(new TextCard("Über welche Länge erstreckt sich das Uralgebirge?", "2.400 Kilometer", "Strecke des Uralgebirges", false));
        erdkundeL.add(new MultipleChoiceCard("Welche dieser Städte liegt am nördlichsten?", new String[]{"Adelaide", "Perth", "Melbourne", "Brisbane"}, new int[]{3}, "Nördlichste Stadt Australiens", false));
        erdkundeL.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten", false));

        List<Tag> erdkundeTags = new ArrayList<>() {
            {
                add(new Tag("Deutschland"));
                add(new Tag("Australien"));
                add(new Tag("Länder"));
            }
        };

        List<Category> erdkundeC = new ArrayList<>() {
            {
                add(new Category("Erdkunde"));
                add(new Category("Schule"));
            }
        };

        List<Card> spanischL = new ArrayList<>();
        //Karten 2: Spanisch
        spanischL.add(new AudioCard(null, "Comer", "Comer", "Essen", false, true));
        spanischL.add(new AudioCard(null, "Trinken", "Trinken", "Beber", false, true));
        spanischL.add(new AudioCard(null, "Taufen", "Taufen", "bautizar", false, true));


        List<Tag> spanischTags = new ArrayList<>() {
            {
                add(new Tag("Essen"));
                add(new Tag("Anderes"));
            }
        };

        List<Category> spanischC = new ArrayList<>() {
            {
                add(new Category("Spanisch"));
                add(new Category("Schule"));
            }
        };


        List<Card> technikL = new ArrayList<>();
        //Karten 3: Technik Quiz
        technikL.add(new MultipleChoiceCard("Mag sein, dass das iPhone 2007 die Ära der Smartphones erst so richtig begründet hat. Aber das IBM Simon (hier in der Mitte) war tatsächlich das allererste Smartphone mit Touch Display. Wann kam es auf den Markt??", new String[]{"1989", "1994", "2001"}, new int[]{1}, "Erstes Smartphone", false));
        technikL.add(new MultipleChoiceCard("Google Earth ist weltweit bekannt. Doch eine Berliner Agentur schuf bereits Anfang der Neunziger ein ähnliches Produkt, wie hieß der digitale Globus der Berliner??", new String[]{"Terravision", "Globetrotter", "Digiglobe"}, new int[]{0}, "Das erste Google Earth", false));
        technikL.add(new TrueFalseCard("Logitech produzierte die erste echte Computer-Maus", false, "Produzent erster Computer-Maus", false));
        technikL.add(new TrueFalseCard("http steht für Hypertext Transfer Protocol", false, "", false));

        List<Tag> technikTags = new ArrayList<>() {
            {
                add(new Tag("Google Earth"));
                add(new Tag("Technikmarken"));
            }
        };

        List<Category> technikC = new ArrayList<>() {
            {
                add(new Category("Technik"));
                add(new Category("Schule"));
            }
        };

        for (Card c : randomL) {
            cardLogic.updateCardData(c, true);
            cardLogic.setTagsToCard(c, tagsRandom);
            categoryLogic.setC2COrCH(c, randomC,false);
        }
        for (Card c : erdkundeL) {
            cardLogic.updateCardData(c, true);
            cardLogic.setTagsToCard(c, erdkundeTags);
            categoryLogic.setC2COrCH(c, erdkundeC,false);
        }
        for (Card c : spanischL) {
            cardLogic.updateCardData(c, true);
            cardLogic.setTagsToCard(c, spanischTags);
            categoryLogic.setC2COrCH(c, spanischC,false);
        }
        for (Card c : technikL) {
            cardLogic.updateCardData(c, true);
            cardLogic.setTagsToCard(c, technikTags);
            categoryLogic.setC2COrCH(c, technikC,false);
        }

    }
}
