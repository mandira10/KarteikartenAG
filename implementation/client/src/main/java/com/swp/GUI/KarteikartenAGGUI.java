package com.swp.GUI;

import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.*;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.Extras.ConfirmationGUI;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.GUI.Extras.ConfirmationGUI.ConfirmationCallback;
import com.swp.GUI.PageManager.PAGES;

import java.util.*;

public class KarteikartenAGGUI extends RenderGUI
{
    private Sidebar pSidebar;
    private NotificationGUI pNotifications;
    private ConfirmationGUI pConfirmations;

    private CategoryPage pCategoryPage;

    public KarteikartenAGGUI()
    {
        PageManager.init(this);
        hideChildren(true); //Manually render

        importTestData();

        ImageDescriptionCardAnswer[] answers = new ImageDescriptionCardAnswer[] {
            new ImageDescriptionCardAnswer("Orangenblatt", 75, 5),
            new ImageDescriptionCardAnswer("Orange",       61, 26),
            new ImageDescriptionCardAnswer("Nase",         40, 67),
            new ImageDescriptionCardAnswer("Hand",         82, 58),
            new ImageDescriptionCardAnswer("Fuß",          62, 89),
        };
        ImageDescriptionCard card = new ImageDescriptionCard("What is orange ket?", answers, "Importance of kets", "textures/orange-ket.png", true);

        //EditCardPage page = (EditCardPage)PageManager.getPage(PAGES.CARD_EDIT);
        //page.editCard(card);
        //PageManager.viewPage(PAGES.CARD_EDIT);

        ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage(PAGES.CARD_SINGLEVIEW);
        page.setCard(card);
        PageManager.viewPage(PAGES.CARD_SINGLEVIEW);


        pConfirmations = ConfirmationGUI.getInstance();
        addElement(pConfirmations);
        
        pNotifications = NotificationGUI.getInstance();
        addElement(pNotifications);


        //NotificationGUI.addNotification("Info Message with a little more text test test test test test test test", NotificationType.INFO, 5);
        //NotificationGUI.addNotification("Warning Message", NotificationType.WARNING, 10);
        //NotificationGUI.addNotification("Error Message", NotificationType.ERROR, 5);
        //NotificationGUI.addNotification("Critical Error Message", NotificationType.CRITICAL, 5);
        //NotificationGUI.addNotification("Debug Message", NotificationType.DEBUG, 3);
        //NotificationGUI.addNotification("Some Connection Message/Error", NotificationType.CONNECTION, 3);

        pSidebar = new Sidebar();
        pSidebar.setSize(new ivec2(60, 100));
        pSidebar.setSizeInPercent(false, true);
        addElement(pSidebar);
    }


    @Override
    public void renderextra()
    {
        PageManager.render();
        pSidebar.render();
        pConfirmations.render();
        pNotifications.render();
    }

    @Override
    public void updateextra()
    {
        pConfirmations.update();
        if(pConfirmations.isHidden())
        {
            pNotifications.update();
            pSidebar.update();
            PageManager.update();
        }
    }

    private void importTestData() {

                List<Card> randomL = new ArrayList<>();
                //randomL.add(new ImageDescriptionCard("Some Image Description Question", new ImageDescriptionCardAnswer[]{}, "ImageDescriptionTitle", "textures/orange-ket.png", false));
                randomL.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", "textures/orange-ket.png", "ImageTestCardTitle", false, true));
                Set<Tag> tagsRandom = new HashSet<>() {
                    {
                        add(new Tag("toll"));
                        add(new Tag("cool"));
                    }
                };

                Set<Category> randomC = new HashSet<>() {
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
                //erdkundeL.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten", false));

                Set<Tag> erdkundeTags = new HashSet<>() {
                    {
                        add(new Tag("Deutschland"));
                        add(new Tag("Australien"));
                        add(new Tag("Länder"));
                    }
                };

                Set<Category> erdkundeC = new HashSet<>() {
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


                Set<Tag> spanischTags = new HashSet<>() {
                    {
                        add(new Tag("Essen"));
                        add(new Tag("Anderes"));
                    }
                };

                Set<Category> spanischC = new HashSet<>() {
                    {
                        add(new Category("Spanisch"));
                        add(new Category("Schule"));
                    }
                };


                List<Card> technikL = new ArrayList<>();
                //Karten 3: Technik Quiz
                technikL.add(new MultipleChoiceCard("Mag sein, dass das iPhone 2007 die Ära der Smartphones erst so richtig begründet hat. Aber das IBM Simon (hier in der Mitte) war tatsächlich das allererste Smartphone mit Touch Display. Wann kam es auf den Markt??", new String[]{"1989", "1994", "2001"}, new int[]{1}, "Erstes Smartphone", false));
                technikL.add(new MultipleChoiceCard("Google Earth ist weltweit bekannt. Doch eine Berliner Agentur schuf bereits Anfang der Neunziger ein ähnliches Produkt, wie hieß der digitale Globus der Berliner??", new String[]{"Terravision", "Globetrotter", "Digiglobe"}, new int[]{0}, "Das erste Google Earth", false));
                //technikL.add(new TrueFalseCard("Logitech produzierte die erste echte Computer-Maus", false, "Produzent erster Computer-Maus", false));
                //technikL.add(new TrueFalseCard("http steht für Hypertext Transfer Protocol", false, "", false));

                Set<Tag> technikTags = new HashSet<>() {
                    {
                        add(new Tag("Google Earth"));
                        add(new Tag("Technikmarken"));
                    }
                };

                Set<Category> technikC = new HashSet<>() {
                    {
                        add(new Category("Technik"));
                        add(new Category("Schule"));
                    }
                };

                for (Card c : randomL) {
                    CardController.updateCardData(c, true);
                    CardController.setTagsToCard(c, tagsRandom);
                    CategoryController.setCategoriesToCard(c, randomC);
                }
                for (Card c : erdkundeL) {
                    CardController.updateCardData(c, true);
                    CardController.setTagsToCard(c, erdkundeTags);
                    CategoryController.setCategoriesToCard(c, erdkundeC);
                }
                for (Card c : spanischL) {
                    CardController.updateCardData(c, true);
                    CardController.setTagsToCard(c, spanischTags);
                    CategoryController.setCategoriesToCard(c, spanischC);
                }
                for (Card c : technikL) {
                    CardController.updateCardData(c, true);
                    CardController.setTagsToCard(c, technikTags);
                    CategoryController.setCategoriesToCard(c, technikC);
                }

            }
}