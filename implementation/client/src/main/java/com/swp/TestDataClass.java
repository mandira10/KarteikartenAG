package com.swp;

import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasse speist einzelne Testdaten in die GUI bei Start ein.
 */
public class TestDataClass {


    public static void importTestData() {
        final CategoryController categoryController = CategoryController.getInstance();
        final CardController cardController = CardController.getInstance();

        Category randomCategory = new Category("Random");
        Category schuleCategory = new Category("Schule");
        Category technikCategory = new Category("Technik");
        Category spanischCategory = new Category("Spanisch");
        Category erdkundeCategory = new Category("Erdkunde");

        categoryController.editCategoryHierarchy(schuleCategory, new ArrayList<Category>(), new ArrayList<Category>() {
            {
                add(spanischCategory);
                add(erdkundeCategory);
            }
        }, new SingleDataCallback<Boolean>() {
            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onSuccess(Boolean data) {
            }
        });

        for (Category category : new Category[]{randomCategory, schuleCategory, technikCategory, spanischCategory, erdkundeCategory}) {
            categoryController.updateCategoryData(category, true, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
        }


        List<Card> randomL = new ArrayList<>();
        //randomL.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[]{}, "ImageDescriptionTitle", "textures/orange-ket.png", false));
        randomL.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", "textures/orange-ket.png", "ImageTestCardTitle", false, true));

        for (Card c : randomL) {
            CardController.getInstance().updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
            cardController.setTagsToCard(c, new ArrayList<>() {
                {
                    add(new Tag("toll"));
                    add(new Tag("cool"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(randomCategory);
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
        }


        List<Card> erdkundeL = new ArrayList<>();
        //Karten 2: Erdkunde Fragen MC
        erdkundeL.add(new MultipleChoiceCard("Welches der folgenden ist das flächenmäßig kleinste Bundesland?", new String[]{"Thüringen", "Sachsen", "Saarland"}, new int[]{2}, "Bundesländer Größe", false));
        erdkundeL.add(new MultipleChoiceCard("Welcher See liegt nicht in Bayern?", new String[]{"Ammersee", "Schweriner See", "Starnberger See"}, new int[]{0}, "Sees in Bayern", false));
        erdkundeL.add(new TextCard("Über welche Länge erstreckt sich das Uralgebirge?", "2.400 Kilometer", "Strecke des Uralgebirges", false));
        erdkundeL.add(new MultipleChoiceCard("Welche dieser Städte liegt am nördlichsten?", new String[]{"Adelaide", "Perth", "Melbourne", "Brisbane"}, new int[]{3}, "Nördlichste Stadt Australiens", false));
        erdkundeL.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten", false));

        for (Card c : erdkundeL) {
            CardController.getInstance().updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Deutschland"));
                    add(new Tag("Australien"));
                    add(new Tag("Länder"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(erdkundeCategory);
                    add(schuleCategory);
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
        }


        List<Card> spanischL = new ArrayList<>();
        //Karten 2: Spanisch
        spanischL.add(new AudioCard(null, "Comer", "Comer", "Essen", false, true));
        spanischL.add(new AudioCard(null, "Trinken", "Trinken", "Beber", false, true));
        spanischL.add(new AudioCard(null, "Taufen", "Taufen", "bautizar", false, true));

        for (Card c : spanischL) {
            CardController.getInstance().updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Essen"));
                    add(new Tag("Anderes"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(spanischCategory);
                    add(schuleCategory);
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
        }


        List<Card> technikL = new ArrayList<>();
        //Karten 3: Technik Quiz
        technikL.add(new MultipleChoiceCard("Mag sein, dass das iPhone 2007 die Ära der Smartphones erst so richtig begründet hat. Aber das IBM Simon (hier in der Mitte) war tatsächlich das allererste Smartphone mit Touch Display. Wann kam es auf den Markt??", new String[]{"1989", "1994", "2001"}, new int[]{1}, "Erstes Smartphone", false));
        technikL.add(new MultipleChoiceCard("Google Earth ist weltweit bekannt. Doch eine Berliner Agentur schuf bereits Anfang der Neunziger ein ähnliches Produkt, wie hieß der digitale Globus der Berliner??", new String[]{"Terravision", "Globetrotter", "Digiglobe"}, new int[]{0}, "Das erste Google Earth", false));
        technikL.add(new TrueFalseCard("Logitech produzierte die erste echte Computer-Maus", false, "Produzent erster Computer-Maus", false));
        technikL.add(new TrueFalseCard("http steht für Hypertext Transfer Protocol", false, "", false));

        for (Card c : technikL) {
            CardController.getInstance().updateCardData(c, true, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Google Earth"));
                    add(new Tag("Technikmarken"));
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(technikCategory);
                    add(schuleCategory);
                }
            }, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                }

                @Override
                public void onFailure(String msg) {
                }
            });
        }
    }
}
