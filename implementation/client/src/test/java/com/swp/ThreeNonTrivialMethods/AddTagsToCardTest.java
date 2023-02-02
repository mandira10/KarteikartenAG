package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.CardController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.*;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatcher;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * In dieser Testklasse werden alle Methoden getestet, welche bei dem nicht-trivialen Anwendungsfall 'N3-8' Karteikarte Schlagwörter (Tags)
 * zuordnen involviert sind.
 * In der GUI können bei Erstellen oder bei Bearbeiten einer Karte die Tags angepasst werden.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

        Zum Abrufen:
        - tagRepository.getTagsToCard(card)
         */


        CardController cardController;
        CardLogic cardLogic;
        TagRepository tagRepository;
        CardToTagRepository cardToTagRepository;

        //Testinstanz Card
        Card card1 = new TrueFalseCard("Testfrage für Tags 1",false, "Testtitel für Testtags1");
        Card card2 = new TrueFalseCard("Testfrage für Tags 2",false, "Testtitel für Testtags2");
        //Testinstanz Tag bereits in DB enthalten
        Tag tagExisting = new Tag("tagExisting");



        @BeforeAll
        public static void before()
        {
                PersistenceManager.init("KarteikartenDBTest");
                German.getInstance().activate();
                ControllerThreadPool.getInstance().synchronizedTasks(true);
        }




        @BeforeEach
        public void setup() {
            cardController = CardController.getInstance();
            cardLogic = CardLogic.getInstance();
            tagRepository = TagRepository.getInstance();
            cardToTagRepository = CardToTagRepository.getInstance();
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
        @Order(0)
        public void testCreateTagFunctionAllPossibilitiesForLogic() {
                //mock DataCallback
                DataCallback<Tag> mockDataCallback = mock(DataCallback.class);
                SingleDataCallback<Boolean> mockSingleDataCallback = mock(SingleDataCallback.class);

                //SetUp der DB, eine Karte und ein Tag hinzufügen
                cardLogic.updateCardData(card1,true); 
                TagRepository.startTransaction();
                tagRepository.save(tagExisting);
                TagRepository.commitTransaction();
                 
                //Testfall 1: Karte hat noch keine Tags und soll nun 2 hinzugefügt bekommen.
                assertTrue(cardLogic.getTagsToCard(card1).isEmpty());
                on(cardController).set("cardLogic",cardLogic);
                Tag tagToAdd1 = new Tag("TestTag1");
                Tag tagToAdd2 = new Tag("TestTag2");
                ArrayList<Tag> tags = new ArrayList<>() {
                        {
                                add(tagToAdd1);
                                add(tagToAdd2);
                        }
                };



                assertDoesNotThrow(() -> cardController.setTagsToCard(card1, tags, mockSingleDataCallback));
                verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

                        @Override public boolean matches(Boolean aBoolean) {
                                assertTrue(aBoolean);
                                return true;
                        }
                }));



                assertDoesNotThrow(() -> cardController.getTagsToCard(card1, mockDataCallback));
                verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Tag>>() {
                        @Override
                        public boolean matches(List<Tag> tags) {
                                assertEquals(2,tags.size());
                                assertTrue(tags.stream().anyMatch(f -> f.getVal().equals(tagToAdd1.getVal())));
                                assertTrue(tags.stream().anyMatch(f -> f.getVal().equals(tagToAdd2.getVal())));
                                return true;
                        }
                }));

                reset(mockDataCallback);
                reset(mockSingleDataCallback);

                //Testfall 2: Karte hat nun 2 Tags, kriegt diese erneut übermittelt.
                assertDoesNotThrow(() ->  cardController.setTagsToCard(card1, tags, mockSingleDataCallback));
                verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

                        @Override public boolean matches(Boolean aBoolean) {
                                assertTrue(aBoolean);
                                return true;
                        }
                }));

                assertDoesNotThrow(() -> cardController.getTagsToCard(card1, mockDataCallback));
                verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Tag>>() {
                        @Override
                        public boolean matches(List<Tag> tags) {
                                assertEquals(2,tags.size());
                                //prüfe ob Tags die gleichen sind
                                Tag tag1 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                                Tag tag2 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                                assertEquals(tag1,tagToAdd1);
                                assertEquals(tag2,tagToAdd2);
                                return true;
                        }
                }));

                reset(mockDataCallback);
                reset(mockSingleDataCallback);
                
                //Testfall 3: Karte hat weiterhin 2 Tags, kriegt nun 2 neue (tagToAdd3, tagToAdd4) mit den 2 alten übermittelt, wobei einer vom Namen her bereits Teil der DB ist (tagToAdd3).
                Tag tagToAdd3 = new Tag("tagExisting");
                Tag tagToAdd4 = new Tag("TestTag3");
                Tag tagToAdd5 = new Tag("TestTag2");
                List<Tag> tagsNewToAdd = Arrays.asList( tagToAdd1,tagToAdd5,tagToAdd3,tagToAdd4);
                List<Tag> finalTagsNewToAdd = tagsNewToAdd;
                assertDoesNotThrow(() -> cardController.setTagsToCard(card1, finalTagsNewToAdd,mockSingleDataCallback));
                verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

                        @Override public boolean matches(Boolean aBoolean) {
                                assertTrue(aBoolean);
                                return true;
                        }
                }));

                assertDoesNotThrow(() -> cardController.getTagsToCard(card1, mockDataCallback));
                verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Tag>>() {
                        @Override
                        public boolean matches(List<Tag> tags) {
                                assertEquals(4, tags.size());
                                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                                Tag tag1 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                                Tag tag2 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                                Tag tag3 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd3.getVal())).findFirst().get();
                                Tag tag4 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd4.getVal())).findFirst().get();
                                assertEquals(tag1,tagToAdd1);
                                assertEquals(tag2,tagToAdd2);
                                assertEquals(tag3,tagToAdd3);
                                assertEquals(tag4,tagToAdd4);
                                return true;
                        }
                }));

                reset(mockDataCallback);
                reset(mockSingleDataCallback);
                
                //Testfall 4: Karte hat nun 4 Tags, TagToAdd1 und TagToAdd3 sollen nun wieder aus der Kartenverbindung entfernt werden.
                tagsNewToAdd = Arrays.asList( tagToAdd2,tagToAdd4);
                List<Tag> finalTagsNewToAdd1 = tagsNewToAdd;
                assertDoesNotThrow(() ->cardController.setTagsToCard(card1, finalTagsNewToAdd1, mockSingleDataCallback));
                verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

                        @Override public boolean matches(Boolean aBoolean) {
                                assertTrue(aBoolean);
                                return true;
                        }
                }));

                assertDoesNotThrow(() -> cardController.getTagsToCard(card1, mockDataCallback));
                verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Tag>>() {
                        @Override
                        public boolean matches(List<Tag> tags) {
                                assertEquals(2, tags.size());

                                //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                                assertTrue(tags.stream().noneMatch(tag -> tag.getVal().equals(tagToAdd1.getVal())));
                                assertTrue(tags.stream().noneMatch(tag -> tag.getVal().equals(tagToAdd3.getVal())));
                                Tag tag2 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd2.getVal())).findFirst().get();
                                Tag tag4 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd4.getVal())).findFirst().get();
                                assertEquals(tag2,tagToAdd2);
                                assertEquals(tag4,tagToAdd4);
                                return true;
                        }
                }));

                reset(mockDataCallback);
                reset(mockSingleDataCallback);
                
                //Testfall 5: Karte hat TagToAdd1 und TagToAdd3 im Inventar, TagToAdd2 und TagToAdd4 sollen nun wieder hinzugefügt werden, während
                //TagToAdd1 und TagToAdd3 gelöscht werden.
                tagsNewToAdd = Arrays.asList( tagToAdd1,tagToAdd3);
                List<Tag> finalTagsNewToAdd2 = tagsNewToAdd;
                assertDoesNotThrow(() ->  cardController.setTagsToCard(card1, finalTagsNewToAdd2, mockSingleDataCallback));
                verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

                        @Override public boolean matches(Boolean aBoolean) {
                                assertTrue(aBoolean);
                                return true;
                        }
                }));

                assertDoesNotThrow(() -> cardController.getTagsToCard(card1, mockDataCallback));
                verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Tag>>() {
                @Override
                public boolean matches(List<Tag> tags) {
                        assertEquals(2, tags.size());

                        //Prüfe, dass Tags die gleichen vals haben wie die bestehenden und die gleichen sind.
                        assertTrue(tags.stream().noneMatch(tag -> tag.getVal().equals(tagToAdd2.getVal())));
                        assertTrue(tags.stream().noneMatch(tag -> tag.getVal().equals(tagToAdd4.getVal())));
                        Tag tag1 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd1.getVal())).findFirst().get();
                        Tag tag3 = tags.stream().filter(tag -> tag.getVal().equals(tagToAdd3.getVal())).findFirst().get();
                        assertEquals(tag1,tagToAdd1);
                        assertEquals(tag3,tagToAdd3);
                        return true;
                }
        }));



        }

        @Order(1)
        @Test
        public void ControllerGetTagsToCardCardNull() {
                Card card =null;
                DataCallback<Tag> mockDataCallback = mock(DataCallback.class);
                assertDoesNotThrow(() -> cardController.getTagsToCard(card, mockDataCallback));
                verify(mockDataCallback, times(1))
                        .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        }

        @Test
        public void ControllerGetTagsToCardException() {
                CardLogic cardMockLogic = mock(CardLogic.class);
                CardController cardController1 = CardController.getInstance();
                on(cardController1).set("cardLogic",cardMockLogic);
                when(cardMockLogic.getTagsToCard(card2)).thenThrow(new RuntimeException("Mock Error"));
                DataCallback<Tag> mockDataCallback = mock(DataCallback.class);
                assertDoesNotThrow(() -> cardController1.getTagsToCard(card2, mockDataCallback));
                verify(mockDataCallback, times(1))
                        .callFailure(Locale.getCurrentLocale().getString("gettagstocarderror"));
        }

        @Test
        public void ControllerGetEmptyList() {
                cardLogic.updateCardData(card2,true);
                DataCallback<Tag> mockDataCallback = mock(DataCallback.class);
                assertDoesNotThrow(() -> cardController.getTagsToCard(card2, mockDataCallback));
        }

        @Test
        public void ControllerSetTagsToCardCardNull() {
                Card card =null;
                SingleDataCallback<Boolean> sMockDataCallback = mock(SingleDataCallback.class);
                assertDoesNotThrow(() -> cardController.setTagsToCard(card, new ArrayList<>(), sMockDataCallback));
                verify(sMockDataCallback, times(1))
                        .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        }

        @Test
        public void ControllerSetTagsToCardException() {
                ArrayList<Tag> tags = new ArrayList<>();
                CardLogic cardMockLogic = mock(CardLogic.class);
                CardController cardController1 = CardController.getInstance();
                on(cardController1).set("cardLogic",cardMockLogic);
                doThrow(new RuntimeException("Mock")).when(cardMockLogic).setTagsToCard(card2,tags);
                SingleDataCallback<Boolean> sMockDataCallback = mock(SingleDataCallback.class);
                assertDoesNotThrow(() -> cardController1.setTagsToCard(card2, tags, sMockDataCallback));
                verify(sMockDataCallback, times(1))
                        .callFailure(Locale.getCurrentLocale().getString("settagstocarderror"));
                reset(cardMockLogic);
        }


        
        @Test
        public void LogicEmptyOrNullTag() {
                Card card = null;
                assertThrows(IllegalStateException.class, () -> cardLogic.getTagsToCard(card));
        }

        @Test
        public void LogicNullCard() {
                Tag tag = new Tag("");
                assertThrows(IllegalArgumentException.class, () -> cardLogic.setTagsToCard(card2, Arrays.asList(tag)));
                Tag tag1 = new Tag(null);
                assertThrows(IllegalArgumentException.class, () -> cardLogic.setTagsToCard(card2, Arrays.asList(tag1)));
                Card card = null;
                assertThrows(IllegalStateException.class, () -> cardLogic.setTagsToCard(card,Arrays.asList(tag1)));
        }

        @Test
        public void LogicRepoExceptionGetTagsToCard(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                when(tagMockRepo.getTagsToCard(card2)).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
                assertThrows(RuntimeException.class, () -> cardLogic1.getTagsToCard(card2));
                reset(tagMockRepo);
        }

        @Test
        public void LogicRepoExceptionSaveTag(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                when(tagMockRepo.findTag("tagExisting")).thenThrow(new NoResultException());
                when(tagMockRepo.save(tagExisting)).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
                assertThrows(RuntimeException.class, () -> cardLogic1.setTagsToCard(card2, Arrays.asList(tagExisting)));
                reset(tagMockRepo);
        }

        @Test
        public void LogicRepoExceptionFindTag(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                when(tagMockRepo.findTag("tagExisting")).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
                assertThrows(RuntimeException.class, () -> cardLogic1.setTagsToCard(card2, Arrays.asList(tagExisting)));
                reset(tagMockRepo);
        }

        @Test
        public void LogicRepoExceptionCreateCardToTag(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardToTagRepository cardToTagMockRepo = mock(CardToTagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                on(cardLogic1).set("cardToTagRepository", cardToTagMockRepo);
                when(tagMockRepo.findTag("tagExisting")).thenReturn(tagExisting);
                when(cardToTagMockRepo.createCardToTag(card2,tagExisting)).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
                assertThrows(RuntimeException.class, () -> cardLogic1.setTagsToCard(card2, Arrays.asList(tagExisting)));
                reset(tagMockRepo);
                reset(cardToTagMockRepo);
        }

        @Test
        public void LogicRepoExceptionfindSpecificCardToTag(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardToTagRepository cardToTagMockRepo = mock(CardToTagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                on(cardLogic1).set("cardToTagRepository", cardToTagMockRepo);
                List<Tag> existingTags = Arrays.asList(tagExisting);
                List<Tag> newTags = new ArrayList<>();
                when(tagMockRepo.getTagsToCard(card2)).thenReturn(existingTags);
                when(cardToTagMockRepo.findSpecificCardToTag(card2,tagExisting)).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
                assertThrows(RuntimeException.class, () -> cardLogic1.setTagsToCard(card2,newTags));
                reset(tagMockRepo);
                reset(cardToTagMockRepo);
        }

        @Test
        public void LogicRepoExceptionDelete(){
                TagRepository tagMockRepo = mock(TagRepository.class);
                CardToTagRepository cardToTagMockRepo = mock(CardToTagRepository.class);
                CardLogic cardLogic1 = CardLogic.getInstance();
                on(cardLogic1).set("tagRepository", tagMockRepo);
                on(cardLogic1).set("cardToTagRepository", cardToTagMockRepo);
                List<Tag> existingTags = Arrays.asList(tagExisting);
                List<Tag> newTags = new ArrayList<>();
                CardToTag cardToTag = new CardToTag(card2,tagExisting);
                when(tagMockRepo.getTagsToCard(card2)).thenReturn(existingTags);
                when(cardToTagMockRepo.findSpecificCardToTag(card2,tagExisting)).thenReturn(cardToTag);
                doThrow(new RuntimeException("gemockter Datenbankfehler")).when(cardToTagMockRepo).delete(cardToTag);
                assertThrows(RuntimeException.class, () -> cardLogic1.setTagsToCard(card2,newTags));
                reset(tagMockRepo);
                reset(cardToTagMockRepo);
        }

        @Test
        public void PersistenceTagNotFound() {
                TagRepository.startTransaction();
                assertThrows(NoResultException.class, () -> tagRepository.findTag(""));
                TagRepository.commitTransaction();
        }
        @Test
        public void PersistenceCardToTagNotFound() {
                TagRepository.startTransaction();
                assertThrows(NoResultException.class, () -> cardToTagRepository.findSpecificCardToTag(null,null));
                TagRepository.commitTransaction();
        }

        @Test
        public void PersistenceCardToTagsEmpty() {
                cardLogic.updateCardData(card2,true);
                TagRepository.startTransaction();
                assertDoesNotThrow(() -> tagRepository.getTagsToCard(card2));
                assertTrue(tagRepository.getTagsToCard(card2).isEmpty());
                TagRepository.commitTransaction();
        }



        @Test
        public void PersistenceNoTransaction() {
                assertThrows(NullPointerException.class, () -> tagRepository.getTagsToCard(card1));
                assertThrows(NullPointerException.class, () -> tagRepository.findTag("keine Transaktion!"));
                assertThrows(NullPointerException.class, () -> cardToTagRepository.findSpecificCardToTag(card1,tagExisting));
                assertThrows(NullPointerException.class, () -> cardToTagRepository.createCardToTag(card1,tagExisting));
                assertThrows(IllegalStateException.class, () -> tagRepository.save(tagExisting));
                assertThrows(IllegalStateException.class, () -> cardToTagRepository.delete(new CardToTag()));

        }


}
