package com.swp;

import com.gumse.tools.Toolbox;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.DataModel.Tag;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryUtil;

/**
 * Klasse speist einzelne Testdaten in die GUI bei Start ein.
 */
public class TestDataClass 
{
    /**
     * Importiert Test/Demo daten in die aktuelle Datenbank
     */
    public static void importTestData()
    {
        final CategoryController categoryController = CategoryController.getInstance();
        final CardController cardController = CardController.getInstance();
        final StudySystemController studySystemController = StudySystemController.getInstance();

        Category randomCategory = new Category("Random");
        Category schuleCategory = new Category("Schule");
        Category technikCategory = new Category("Technik");
        Category spanischCategory = new Category("Spanisch");
        Category erdkundeCategory = new Category("Erdkunde");


        for (Category category : new Category[]{randomCategory, schuleCategory, technikCategory, spanischCategory, erdkundeCategory})
            categoryController.updateCategoryData(category, true, false, null);
        categoryController.editCategoryHierarchy(schuleCategory, new ArrayList<Category>(), new ArrayList<Category>() {
            {
                add(spanischCategory);
                add(erdkundeCategory);
            }
        }, null);


        List<Card> randomL = new ArrayList<>();
        ByteBuffer imageBuffer = Toolbox.loadResourceToByteBuffer("textures/testimage.png", KarteikartenAG.class);
        byte[] imgdata = new byte[imageBuffer.remaining()];
        imageBuffer.get(imgdata);
        MemoryUtil.memFree(imageBuffer);
        randomL.add(new ImageTestCard("Some Image Test Question", "Correct Image Test Answer", imgdata, "ImageTestCardTitle", false));

        ImageDescriptionCard imgdesccard = new ImageDescriptionCard("What is orange ket?", new ArrayList<>(), "Importance of kets", imgdata);
        List<ImageDescriptionCardAnswer> answers = new ArrayList<>();
        answers.add(new ImageDescriptionCardAnswer("Orangenblatt", 75, 5, imgdesccard));
        answers.add(new ImageDescriptionCardAnswer("Orange",       61, 26, imgdesccard));
        answers.add(new ImageDescriptionCardAnswer("Nase",         40, 67, imgdesccard));
        answers.add(new ImageDescriptionCardAnswer("Hand",         82, 58, imgdesccard));
        answers.add(new ImageDescriptionCardAnswer("Fuß",          62, 89, imgdesccard));
        imgdesccard.setAnswers(answers);
        randomL.add(imgdesccard);


        ByteBuffer audioBuffer = Toolbox.loadResourceToByteBuffer("audios/testaudio.wav", KarteikartenAG.class);
        byte[] audiodata = new byte[audioBuffer.remaining()];
        audioBuffer.get(audiodata);
        MemoryUtil.memFree(audioBuffer);
        randomL.add(new AudioCard(audiodata, "AudioCard", "Some Audio Question", "Correct Audio Test Answer", false));

        String testReferences = 
            "ctg;" + randomCategory.getUuid() + ";random random\n" +
            "crd;" + randomL.get(0).getUuid() + ";Weird card I found on the street\n" +
            "htm;https://www.google.de;Some obscure searchengine \n" +
            "fil;./pom.xml;the krabby patty secret formula";

        for (Card c : randomL) {
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<>() {
                {
                    add(new Tag("toll"));
                    add(new Tag("cool"));
                }
            }, null);

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(randomCategory);
                }
            }, null);
        }


        List<Card> erdkundeL = new ArrayList<>();
        //Karten 2: Erdkunde Fragen MC
        erdkundeL.add(new MultipleChoiceCard("Welches der folgenden ist das flächenmäßig kleinste Bundesland?", new String[]{"Thüringen", "Sachsen", "Saarland"}, new int[]{2}, "Bundesländer Größe"));
        erdkundeL.add(new MultipleChoiceCard("Welcher See liegt nicht in Bayern?", new String[]{"Ammersee", "Schweriner See", "Starnberger See"}, new int[]{0}, "Sees in Bayern"));
        erdkundeL.add(new TextCard("Über welche Länge erstreckt sich das Uralgebirge?", "2.400 Kilometer", "Strecke des Uralgebirges"));
        erdkundeL.add(new MultipleChoiceCard("Welche dieser Städte liegt am nördlichsten?", new String[]{"Adelaide", "Perth", "Melbourne", "Brisbane"}, new int[]{3}, "Nördlichste Stadt Australiens"));
        erdkundeL.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten"));

        for (Card c : erdkundeL) {
            c.setReferences(testReferences);
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Deutschland"));
                    add(new Tag("Australien"));
                    add(new Tag("Länder"));
                }
            }, null);
            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(erdkundeCategory);
                    add(schuleCategory);
                }
            }, null);
        }


        List<Card> spanischL = new ArrayList<>();
        //Karten 2: Spanisch
        spanischL.add(new AudioCard(null, "Comer", "Comer", "Essen", false));
        spanischL.add(new AudioCard(null, "Trinken", "Trinken", "Beber", false));
        spanischL.add(new AudioCard(null, "Taufen", "Taufen", "bautizar", false));

        for (Card c : spanischL) {
            c.setReferences(testReferences);
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Essen"));
                    add(new Tag("Anderes"));
                }
            }, null);

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(spanischCategory);
                    add(schuleCategory);
                }
            }, null);
        }


        List<Card> technikL = new ArrayList<>();
        //Karten 3: Technik Quiz
        technikL.add(new MultipleChoiceCard("Das IBM Simon war das allererste Smartphone mit Touch Display. Wann kam es auf den Markt??", new String[]{"1989", "1994", "2001"}, new int[]{1}, "Erstes Smartphone"));
        technikL.add(new MultipleChoiceCard("Google Earth ist weltweit bekannt. Doch eine Berliner Agentur schuf bereits Anfang der Neunziger ein ähnliches Produkt, wie hieß es?", new String[]{"Terravision", "Globetrotter", "Digiglobe"}, new int[]{0}, "Das erste Google Earth"));
        technikL.add(new TrueFalseCard("Logitech produzierte die erste echte Computer-Maus", false, "Produzent erster Computer-Maus"));
        technikL.add(new TrueFalseCard("http steht für Hypertext Transfer Protocol", false, "HTTP"));

        for (Card c : technikL) {
            c.setReferences(testReferences);
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("Google Earth"));
                    add(new Tag("Technikmarken"));
                }
            }, null);

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(technikCategory);
                    add(schuleCategory);
                }
            }, null);
        }

        List<Card> zweitausender = new ArrayList<>();
        //Karten zweitausender
        zweitausender.add(new MultipleChoiceCard("In welchem Jahr kam die erste Harry-Potter-Verfilmung in die Kinos?", new String[]{"2005", "2007", "2001", "2003"}, new int[]{2}, "Erster Harry Potter Film"));
        zweitausender.add(new MultipleChoiceCard("Welcher Papst starb 2005", new String[]{"gar keiner", "Johannes Paul", "Benedikt XVI","Johannes Paul II"}, new int[]{3}, "Papst Tod"));
        zweitausender.add(new MultipleChoiceCard("Mit welchem Film gewann Peter Jackson 2004 den Oscar für den besten Film?", new String[]{"Mulan", "Der Herr der Ringe: Die Rückkehr des Königs", "Harry Potter und der Stein der Weisen", "Der Hobbit eine unerwartete Reise"}, new int[]{1}, "Peter Jacksons Oscar"));
        zweitausender.add(new MultipleChoiceCard("Wo fanden 2008 die Olympischen Sommerspiele statt?", new String[]{"London", "Peking", "Athen", "Sydney"}, new int[]{1}, "Olympia 2088"));
        zweitausender.add(new MultipleChoiceCard("Wer wurde am 7. November 2000 zum 43. US-Präsidenten gewählt??", new String[]{"George W.Bush", "Barack Obama", "Donald Trump", "Angela Merkel"}, new int[]{0}, "USA Präsident 2000"));
        zweitausender.add(new MultipleChoiceCard("Welches Technik Produkt kam 2007 erstmals auf den Markt?", new String[]{"GoPro", "Drohne", "Sony Ericsson W800", "iPhone"}, new int[]{3}, "Technik Produkt 2007"));
        zweitausender.add(new MultipleChoiceCard("Wer gewann 2006 bei der Fußball-WM in Deutschland den Weltmeistertitel??", new String[]{"Deutschland", "Spanien", "Russland", "Italien"}, new int[]{3}, "Fußball WM Deutschland"));

        for (Card c : zweitausender) {
            c.setReferences(testReferences);
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("2000er"));
                    add(new Tag("Alles mögliche"));
                }
            }, null);

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(new Category("Durcheinander"));
                    add(new Category("Jahre"));
                }
            }, null);
        }

        List<Card> serien = new ArrayList<>();
        //Karten zweitausender
        serien.add(new TrueFalseCard("Homer ist der Name des Familienvaters bei Simpson",true,"Die Simpsons"));
        serien.add(new TrueFalseCard("Neil Patrick Harris spielt in HIMYM den Frauenhelden Marshall",false,"HIMYM"));
        serien.add(new TrueFalseCard("Der Königserbe aus Staffel 1 GoT hieß Joffrey",true,"GoT"));
        serien.add(new TrueFalseCard("Walter White entwickelte sich in «Breaking Bad» vom krebskranken Chemie-Lehrer zum gefährlichen Drogen-Baron. Er nannte sich zum Schluss Goldberg",false,"Breaking Bad"));
        serien.add(new TrueFalseCard("Sarah Jessica Parker spielt Carrie in Sex and the City",true,"SATC"));
        serien.add(new TrueFalseCard("Chandra Wilson spielt in Greys Anatomy Arizona Robbins",false,"Greys Anatomy"));

        for (Card c : serien) {
            c.setReferences(testReferences);
            cardController.updateCardData(c, true, null);
            cardController.setTagsToCard(c, new ArrayList<Tag>() {
                {
                    add(new Tag("serien"));
                    add(new Tag("Alles mögliche"));
                }
            }, null);

            categoryController.setCategoriesToCard(c, new ArrayList<Category>() {
                {
                    add(new Category("Durcheinander"));
                }
            }, null);
        }


        //TestData For Decks

        StudySystem studySystem1 = new LeitnerSystem("Spanisch", StudySystem.CardOrder.ALPHABETICAL);
        studySystemController.updateStudySystemData(null, studySystem1, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(spanischL,studySystem1, null);

        StudySystem studySystem2 = new TimingSystem("Erdkunde", StudySystem.CardOrder.RANDOM,5);
        studySystemController.updateStudySystemData(null, studySystem2, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(erdkundeL,studySystem2, null);

        StudySystem studySystem3 = new VoteSystem("Technik", StudySystem.CardOrder.ALPHABETICAL,7);
        studySystemController.updateStudySystemData(null, studySystem3, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(technikL,studySystem3, null);

        StudySystem studySystem4 = new VoteSystem("2000er", StudySystem.CardOrder.REVERSED_ALPHABETICAL,5);
        studySystemController.updateStudySystemData(null, studySystem4, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(zweitausender, studySystem4, null);

        StudySystem studySystem5 = new LeitnerSystem("Serien", StudySystem.CardOrder.ALPHABETICAL);
        studySystemController.updateStudySystemData(null, studySystem5, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(serien,studySystem5, null);

        StudySystem studySystem6 = new LeitnerSystem("Random", StudySystem.CardOrder.ALPHABETICAL);
        studySystemController.updateStudySystemData(null, studySystem6, true, false,null);
        studySystemController.moveAllCardsForDeckToFirstBox(randomL, studySystem6, null);



        //TestData for Hierarchy
        Category gymnasium = new Category("Gymnasium");
        Category oberschule = new Category("Oberschule");
        Category kunst = new Category("Kunst");
        Category biologie = new Category("Biologie");
        Category klasse10 = new Category("Klasse10");
        Category klasse11 = new Category("Klasse11");
        Category fotosynthese = new Category("Fotosynthese");
        Category genetik = new Category("Genetik");
        Category other = new Category("Other");
        Category other2 = new Category("Other 2");
        Category other3 = new Category("Other 3");
        List<Category> categories = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
                add(kunst);
                add(biologie);
                add(klasse11);
                add(klasse10);
                add(fotosynthese);
                add(genetik);
                add(other);
                add(other2);
                add(other3);
            }
        };
        for (Category c : categories)
            categoryController.updateCategoryData(c, true, false, null);

        List<Category> childKunst = new ArrayList<>() {
            {
                add(klasse11);
                add(klasse10);
            }
        };
        List<Category> childsBiologie = new ArrayList<>() {
            {
                add(klasse11);
                add(klasse10);
            }
        };
        List<Category> childsklasse11 = new ArrayList<>() {
            {
                add(fotosynthese);
                add(genetik);
                add(other);
                add(other2);
            }
        };
        List<Category> parentsKunst = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
            }
        };
        List<Category> parentsBiologie = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
            }
        };
        List<Category> others = new ArrayList<>() {
            {
                add(other3);
            }
        };
        categoryController.editCategoryHierarchy(kunst, parentsKunst, childKunst, null);
        categoryController.editCategoryHierarchy(biologie, parentsBiologie, childsBiologie, null);
        categoryController.editCategoryHierarchy(klasse11, new ArrayList<Category>(), childsklasse11, null);
        categoryController.editCategoryHierarchy(other2, new ArrayList<Category>(), others, null);

        //cardController.getCardsToShow(0, 16, new DataCallback<CardOverview>() {
        //    @Override protected void onSuccess(List<CardOverview> data) 
        //    {
        //        cardController.exportCards(data, "./testexport.xml", ExportFileType.EXPORT_XML, new SingleDataCallback<Boolean>() {
        //            @Override protected void onSuccess(Boolean data) {}
        //            @Override protected void onFailure(String msg) {}
        //        });
        //    }
        //    @Override protected void onFailure(String msg) {}
        //    @Override protected void onInfo(String msg) {}
        //});
    }


}
