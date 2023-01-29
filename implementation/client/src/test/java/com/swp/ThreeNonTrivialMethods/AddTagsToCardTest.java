package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.CardController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.TagRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In dieser Testklasse werden alle Methoden getestet, welche bei dem nicht-trivialen Anwendungsfall 'N3-8' Karteikarte Schlagwörter (Tags)
 * zuordnen involviert sind.
 * In der GUI können bei Erstellen oder bei Bearbeiten einer Karte die Tags angepasst werden.
 */
public class AddTagsToCardTest {


        /*
        GUI:
        In der EditCardPage werden die Tags ausgewählt und in einer Liste hinzugefügt bzw. gelöscht.
        Zunächst werden die bestehenden Tags in der Funktion editCard abgerufen über die CardController.getInstance().getTagsToCard(pNewCard, new DataCallback<Tag>()
        Funktion des Controllers und bei Success zur Karte hinzugefügt (bei Failure sollte eine Notification ausgegeben werden)
        Die TagList ist eine eigene Klasse in der GUI, in der alle Tags aufgelistet drin sind und über die neue Tags aufgenommen werden können.
        Hier wird zudem auf Duplikate von Tags geprüft, so dass nicht zwei Tags mit gleichem Namen übernommen werden.
        Wenn der Nutzer applyChanges() drückt, dann wird die

        Controller:
        Zwei Funktionen müssen getestet werden: getTagsToCard(pNewCard, new DataCallback<Tag>() fürs Aufrufen der bereits bestehenden Tags und
        Und bei applyChanges wird setTagsToCard aufgerufen.

        Für die getTagsToCard müssen 3 Szenarios im Controller geprüft werden (erfolgreich, leere Liste, Exception)
        Für setTagsToCard müssen 2 Funktionen getestet werden (erfolgreich , Exception)

        Logic:
        In der Logic werden folgende Funktionen verwendet und müssen getestet werden:
        - getTagsToCard für das Abrufen der Tags
        - setTagsToCard für das Setzen, dieses arbeitet mit folgenden Hilfsmethoden:
        checkAndCreateTags
        checkAndRemoveTags
        removeCardToTag

         Insgesamt müssen 5 mögliche Kombinationen in der Logik für setTagsToCard getestet werden:
       - es gibt noch keine Tags, alle müssen hinzugefügt werden
       - es gab keine Änderung an den Tags, kein To Do von der Logik
       - es wurden neue Tags hinzugefügt, aber keine alten entfernt
       - es wurden alte Tags entfernt, aber keine neuen hinzugefügt
       - es wurden bestehende Tags gelöscht und neue hinzugefügt

        Persistence:
        - tagRepository.findTag
        - tagRepository.save
        - cardToTagRepository.findSpecificCardToTag
        - cardToTagRepository.createCardToTag
        - cardToTagRepository.delete
         */


        CardController cardController;
        CardLogic cardLogic;
        CardRepository cardRepository;
        TagRepository tagRepository;

        //Testinstanz Card
        Card card1 = new TrueFalseCard("Testfrage für Tags 1",false, "Testtitel für Testtags1");
        
        //Testinstanz Tag bereits in DB enthalten
        Tag tagExisting = new Tag("tagExisting");

        private Locale locale = new Locale("German", "de");
        private int i;
        

        @BeforeAll
        public static void before(){
                ControllerThreadPool.getInstance().synchronizedTasks(true);
        }



        @BeforeEach
        public void setup() {
            cardController = CardController.getInstance();
            cardLogic = CardLogic.getInstance();
            tagRepository = TagRepository.getInstance();


                Locale.setCurrentLocale(locale);
                String filecontent = Toolbox.loadResourceAsString("locale/de_DE.UTF-8", getClass());

                i = 0;
                filecontent.lines().forEach((String line) -> {
                        i++;
                        if(line.replaceAll("\\s","").isEmpty() || line.charAt(0) == '#')
                                return;

                        String[] args = line.split("= ");
                        if(args.length < 1)
                                Output.error("Locale resource for language " + locale.getLanguage() + " is missing a definition at line " + i);
                        String id = args[0].replaceAll("\\s","");
                        String value = args[1];
                        locale.setString(id, value);
                });


        }


        /**
         * Testet die 5 Hinzufüg-Möglichkeiten der Tags zur Testkarte card1. Die Funktionen werden ungemockt von Controller zur Persistence weitergegeben
         * und in der Datenbank gespeichert. Danach erfolgt die Kontrolle mit der ebenfalls in der GUI verwendeten Methoden des Abrufens der Tags für eine spezifische Karte.
         * Initial wird geprüft, dass es noch keine Tags in der Datenbank für die Karte gibt. Einzelne Komponententests finden sich zudem in den
         * Testklassen: CardToTagLogicTests, CardControllerTests, CardRepositoryTest.
         *
         * Werden jeweils über den Controller aufgerufen und dann an die Persistence weitergegeben.
         * Testfall 1: Karte hat noch keine Tags und soll nun 2 (tagToAdd1, tagToAdd2) hinzugefügt bekommen.
         * Testfall 2: Karte hat noch nun 2 Tags, kriegt diese erneut übermittelt (tagToAdd1, tagToAdd2).
         * Testfall 3: Karte hat weiterhin 2 Tags, kriegt nun 2 neue (tagToAdd3, tagToAdd4) mit den 2 alten übermittelt, wobei einer vom Namen her bereits Teil der DB ist (tagToAdd3).
         * Testfall 4: Karte hat nun 4 Tags, TagToAdd1 und TagToAdd3 sollen nun wieder aus der Kartenverbindung entfernt werden.
         * Testfall 5: Karte hat TagToAdd1 und TagToAdd3 im Inventar, TagToAdd2 und TagToAdd4 sollen wieder hinzugefügt werden, während
         *            TagToAdd1 und TagToAdd3 gelöscht werden.
         */
        @Test
        public void testCreateTagFunctionAllPossibilitiesForLogic() {
                //SetUp der DB, eine Karte und ein Tag hinzufügen
                cardLogic.updateCardData(card1,true); 
                TagRepository.startTransaction();
                tagRepository.save(tagExisting);
                TagRepository.commitTransaction();

                final boolean[] addedSuccessfully = new boolean[5];
                final String[] messageException = new String[5];
                final String[] infoException = new String[5];
                final List<Tag>[] tagsForCardInDatabase = new List[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};
                
                //Testfall 1: Karte hat noch keine Tags und soll nun 2 hinzugefügt bekommen.
                assertTrue(cardLogic.getTagsToCard(card1).isEmpty());

                Tag tagToAdd1 = new Tag("TestTag1");
                Tag tagToAdd2 = new Tag("TestTag2");
                ArrayList<Tag> tags = new ArrayList<>() {
                        {
                                add(tagToAdd1);
                                add(tagToAdd2);
                        }
                };


                cardController.setTagsToCard(card1, tags, new SingleDataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean bol) {
                                addedSuccessfully[0] = bol;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[0] = msg;
                        }
                });
                assertNull(messageException[0]);
                assertTrue(addedSuccessfully[0]);




                cardController.getTagsToCard(card1, new DataCallback<Tag>() {
                        @Override
                        public void onSuccess(List<Tag> data) {
                                tagsForCardInDatabase[0] = data;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[0] = msg;
                        }

                        @Override
                        public void onInfo(String msg) {
                                infoException[0] = msg;
                        }
                });
                assertEquals(2, tagsForCardInDatabase[0].size());
                assertNull(messageException[0]);
                assertNull(infoException[0]);

                //Testfall 2: Karte hat noch nun 2 Tags, kriegt diese erneut übermittelt.
                cardController.setTagsToCard(card1, tags, new SingleDataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean bol) {
                                addedSuccessfully[1] = bol;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[1] = msg;
                        }
                });
                assertNull(messageException[1]);
                assertTrue(addedSuccessfully[1]);

                cardController.getTagsToCard(card1, new DataCallback<Tag>() {
                        @Override
                        public void onSuccess(List<Tag> data) {
                                tagsForCardInDatabase[1] = data;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[1] = msg;
                        }

                        @Override
                        public void onInfo(String msg) {
                                infoException[1] = msg;
                        }
                });
                assertEquals(2, tagsForCardInDatabase[1].size());
                assertNull(messageException[1]);
                assertNull(infoException[1]);

                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                Tag tag1 = tagsForCardInDatabase[1].stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                Tag tag2 = tagsForCardInDatabase[1].stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                assertEquals(tag1,tagToAdd1);
                assertEquals(tag2,tagToAdd2);


                //Testfall 3: Karte hat weiterhin 2 Tags, kriegt nun 2 neue (tagToAdd3, tagToAdd4) mit den 2 alten übermittelt, wobei einer vom Namen her bereits Teil der DB ist (tagToAdd3).
                Tag tagToAdd3 = new Tag("tagExisting");
                Tag tagToAdd4 = new Tag("TestTag3");
                Tag tagToAdd5 = new Tag("TestTag2");
                List<Tag> tagsNewToAdd = Arrays.asList( tagToAdd1,tagToAdd5,tagToAdd3,tagToAdd4);
                cardController.setTagsToCard(card1, tagsNewToAdd, new SingleDataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean bol) {
                                addedSuccessfully[2] = bol;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[2] = msg;
                        }
                });
                assertNull(messageException[2]);
                assertTrue(addedSuccessfully[2]);

                cardController.getTagsToCard(card1, new DataCallback<Tag>() {
                        @Override
                        public void onSuccess(List<Tag> data) {
                                tagsForCardInDatabase[2] = data;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[2] = msg;
                        }

                        @Override
                        public void onInfo(String msg) {
                                infoException[2] = msg;
                        }
                });
                assertEquals(4, tagsForCardInDatabase[2].size());
                assertNull(messageException[2]);
                assertNull(infoException[2]);

                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                tag1 = tagsForCardInDatabase[2].stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                tag2 = tagsForCardInDatabase[2].stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                Tag tag3 = tagsForCardInDatabase[2].stream().filter(tag -> tag.getVal().equals(tagToAdd3.getVal())).findFirst().get();
                Tag tag4 = tagsForCardInDatabase[2].stream().filter(tag -> tag.getVal().equals(tagToAdd4.getVal())).findFirst().get();
                assertEquals(tag1,tagToAdd1);
                assertEquals(tag2,tagToAdd2);
                assertEquals(tag3,tagToAdd3);
                assertEquals(tag4,tagToAdd4);


                //Testfall 4: Karte hat nun 4 Tags, TagToAdd1 und TagToAdd3 sollen nun wieder aus der Kartenverbindung entfernt werden.
                tagsNewToAdd = Arrays.asList( tagToAdd2,tagToAdd4);
                cardController.setTagsToCard(card1, tagsNewToAdd, new SingleDataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean bol) {
                                addedSuccessfully[3] = bol;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[3] = msg;
                        }
                });
                assertNull(messageException[3]);
                assertTrue(addedSuccessfully[3]);

                cardController.getTagsToCard(card1, new DataCallback<Tag>() {
                        @Override
                        public void onSuccess(List<Tag> data) {
                                tagsForCardInDatabase[3] = data;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[3] = msg;
                        }

                        @Override
                        public void onInfo(String msg) {
                                infoException[3] = msg;
                        }
                });
                assertEquals(2, tagsForCardInDatabase[3].size());
                assertNull(messageException[3]);
                assertNull(infoException[3]);

                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                assertTrue(tagsForCardInDatabase[3].stream().noneMatch(tag -> tag.getVal().equals(tagToAdd1.getVal())));
                assertTrue(tagsForCardInDatabase[3].stream().noneMatch(tag -> tag.getVal().equals(tagToAdd3.getVal())));
                tag2 = tagsForCardInDatabase[3].stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                tag4 = tagsForCardInDatabase[3].stream().filter(tag -> tag.getVal().equals(tagToAdd4.getVal())).findFirst().get();
                assertEquals(tag2,tagToAdd2);
                assertEquals(tag4,tagToAdd4);

                //Testfall 5: Karte hat TagToAdd1 und TagToAdd3 im Inventar, TagToAdd2 und TagToAdd4 sollen nun wieder hinzugefügt werden, während 
                //TagToAdd1 und TagToAdd3 gelöscht werden.
                tagsNewToAdd = Arrays.asList( tagToAdd1,tagToAdd3);
                cardController.setTagsToCard(card1, tagsNewToAdd, new SingleDataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean bol) {
                                addedSuccessfully[4] = bol;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[4] = msg;
                        }
                });
                assertNull(messageException[4]);
                assertTrue(addedSuccessfully[4]);

                cardController.getTagsToCard(card1, new DataCallback<Tag>() {
                        @Override
                        public void onSuccess(List<Tag> data) {
                                tagsForCardInDatabase[4] = data;
                        }

                        @Override
                        public void onFailure(String msg) {
                                messageException[4] = msg;
                        }

                        @Override
                        public void onInfo(String msg) {
                                infoException[4] = msg;
                        }
                });
                assertEquals(2, tagsForCardInDatabase[4].size());
                assertNull(messageException[4]);
                assertNull(infoException[4]);

                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                assertTrue(tagsForCardInDatabase[4].stream().noneMatch(tag -> tag.getVal().equals(tagToAdd2.getVal())));
                assertTrue(tagsForCardInDatabase[4].stream().noneMatch(tag -> tag.getVal().equals(tagToAdd4.getVal())));
                tag2 = tagsForCardInDatabase[4].stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                tag4 = tagsForCardInDatabase[4].stream().filter(tag -> tag.getVal().equals(tagToAdd3.getVal())).findFirst().get();
                assertEquals(tag2,tagToAdd1);
                assertEquals(tag4,tagToAdd3);
        }


        /**
         * Testet die Funktionalität in der GUI für die Tag-Bearbeitung. Initiales Laden der Tags zur Karte.
         * Prüfung Karte mit Tags, Karte ohne Tags
         */
        @Test
        public void testFunctionGetTagsGUI() {}

        /**
         * Testet die Funktionalität in der GUI für die Tag-Bearbeitung bei Error, wenn Tags geladen werden.
         */
        @Test
        public void testFunctionGUIGetTagsException() {}

        /**
         * Testet die Funktionalität in der GUI für die Tag-Bearbeitung. Setzen neuer Tags.
         * Prüfung leere Liste, Liste mit Tags. Alles weitere bereits getestet.
         */
        @Test
        public void testFunctionSetTagsGUI() {}

        /**
         * Testet die Funktionalität in der GUI für die Tag-Bearbeitung bei Error, wenn neue Tags gesetzt werden.
         */
        @Test
        public void testFunctionGUISetTagsException() {}

        //TODO: incorporate GUI Testing and Controller Exceptions there






}
