package com.swp.Logic.CardLogicTest;

import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToTagRepository;
import com.swp.Persistence.PersistenceManager;
import com.swp.Persistence.TagRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Diese Testklasse untersucht alle CardLogic Funktionen für die Card2Tags als Komponententests.
 * Ausführlichere Tests finden sich in ThreeNonTrivialTestMethods AddTagsToCardTests.
 */

public class CardToTagLogicTests {
    private CardRepository cardRepMock;
    private TagRepository tagRepMock;

    private CardToTagRepository cardToTagRepMock;
    private final CardLogic cardLogic = CardLogic.getInstance();

    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }
    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        tagRepMock = mock(TagRepository.class);
        cardToTagRepMock = mock(CardToTagRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
        on(cardLogic).set("cardToTagRepository",cardToTagRepMock);
        on(cardLogic).set("tagRepository",tagRepMock);
    }

    /**
     * Testet das Hinzufügen eines einzelnen Tags zu einer Karte, wenn diese noch keine Tags hat.
     */
    @Test
    public void testAddOneCardToTagNotExisting(){
        //Testdaten
        Tag tagToAdd = new Tag("TestTag1");
        ArrayList<Tag> TagToAdd = new ArrayList<>() {
            {
                add(tagToAdd);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2");
        ArrayList<Tag> tagsToReturn = new ArrayList<>();

        //Karte hat noch keine Tags
        when(tagRepMock.getTagsToCard(card1)).thenReturn(tagsToReturn);
        //Der angegebene Tag ist noch nicht in der Datenbank gespeichert, mocke das Speichern des Tags
        when(tagRepMock.findTag(tagToAdd.getVal())).thenThrow(NoResultException.class);
        when(tagRepMock.save(tagToAdd)).thenReturn(tagToAdd);
        //Kein tatsächliches Speichern
        CardToTag t1 = new CardToTag(card1,tagToAdd);
        when(cardToTagRepMock.save(new CardToTag(card1, tagToAdd))).thenReturn(t1);

        //Test
        cardLogic.setTagsToCard(card1,TagToAdd);
    }

    /**
     * Testet das Hinzufügen mehrerer Tags zu einer Karte, wenn diese ncoh keine Tags hat.
     */
    @Test
    public void testMoreThanOneNotExistingTagToAdd(){
        //Testdaten
        Tag tagToAdd = new Tag("TestTag1");
        Tag tagToAdd1 = new Tag("TestTag2");
        ArrayList<Tag> tagsToAdd = new ArrayList<>() {
            {
                add(tagToAdd);
                add(tagToAdd1);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2");
        ArrayList<Tag> tagsToReturn = new ArrayList<>();

        //Karte hat noch keine Tags
        when(tagRepMock.getTagsToCard(card1)).thenReturn(tagsToReturn);
        //TagToAdd ist noch nicht in Datenbank gespeichert, mocke das Speichern
        when(tagRepMock.findTag(tagToAdd.getVal())).thenThrow(NoResultException.class);
        when(tagRepMock.save(tagToAdd)).thenReturn(tagToAdd);
        //TagToAdd1 ist bereits gespeichert und kann verwendet werden
        when(tagRepMock.findTag(tagToAdd1.getVal())).thenReturn(tagToAdd1);
        //Kein tatsächliches Speichern
        CardToTag t1 = new CardToTag(card1,tagToAdd1);
        CardToTag t2 = new CardToTag(card1,tagToAdd);
        when(cardToTagRepMock.save(new CardToTag(card1, tagToAdd1))).thenReturn(t1);
        when(cardToTagRepMock.save(new CardToTag(card1, tagToAdd))).thenReturn(t2);

        //Test
        cardLogic.setTagsToCard(card1,tagsToAdd);
    }

    /**
     * Testet, ob nichts getan wird, wenn die übergebenen Tags die gleichen sind, wie die bereits zugehörigen.
     */
    @Test
    public void testExistingOnlyTagsToAdd() {
        //Testdaten
        Tag exTag1 = new Tag("Erdkunde");
        Tag exTag2 = new Tag("Spanisch");
        ArrayList<Tag> tags = new ArrayList<>() {
            {
                add(exTag1);
                add(exTag2);
            }
        };

        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");

        //Die zugehörigen Tags sind die gleichen wie die hinzugefügten
        //kein Handling nötig
        when(tagRepMock.getTagsToCard(card1)).thenReturn(tags);

        //Test
        cardLogic.setTagsToCard(card1, tags);
    }

    /**
     * Testet das Hinzufügen neuer Tags zu einer Karte, wenn diese bereits zugehörige Tags hat.
     */
    @Test
    public void addNewTagsToExistingTags() {
        //Testdaten
        Tag exTag1 = new Tag("Erdkunde");
        Tag exTag2 = new Tag("Spanisch");
        ArrayList<Tag> existingTags = new ArrayList<>() {
            {
                add(exTag1);
                add(exTag2);
            }
        };

        Tag newTag1 = new Tag("Deutsch");
        Tag newTag2 = new Tag("Englisch");

        ArrayList<Tag> tagsToAdd = new ArrayList<>() {
            {
                add(newTag1);
                add(newTag2);
                add(exTag1);
                add(exTag2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");
        //Bereits gespeicherte Tags zu der Karte werden returned
        when(tagRepMock.getTagsToCard(card1)).thenReturn(tagsToAdd);
        //Die neuen Tags sind noch nicht in DB gespeichert, die alten werden gar nicht gesucht
        when(tagRepMock.findTag(newTag1.getVal())).thenThrow(NoResultException.class);
        when(tagRepMock.findTag(newTag2.getVal())).thenThrow(NoResultException.class);
        when(tagRepMock.save(newTag1)).thenReturn(newTag1);
        when(tagRepMock.save(newTag1)).thenReturn(newTag2);
        //Tue nichts beim Hinzufügen der neuen CardToTags
        CardToTag t1 = new CardToTag(card1,newTag1);
        CardToTag t2 = new CardToTag(card1,newTag2);
        when(cardToTagRepMock.save(new CardToTag(card1, newTag1))).thenReturn(t1);
        when(cardToTagRepMock.save(new CardToTag(card1, newTag2))).thenReturn(t2);

        //Test
        cardLogic.setTagsToCard(card1, tagsToAdd);
    }

    /**
     * Testet das manuelle Löschen von Tags zu einer Karte mit bereits bestehenden Tags.
     */
    @Test
    //testdaten
    public void RemoveTagsFromExistingCard() {
        Tag exTag1 = new Tag("Erdkunde");
        Tag exTag2 = new Tag("Spanisch");
        Tag exTag3 = new Tag("Deutsch");
        Tag exTag4 = new Tag("Englisch");
        ArrayList<Tag> existingTags = new ArrayList<>() {
            {
                add(exTag1);
                add(exTag2);
                add(exTag3);
                add(exTag4);
            }
        };

        ArrayList<Tag> tagsToAdd = new ArrayList<>() {
            {
                add(exTag1);
                add(exTag2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");
        //Bereits gespeicherte Tags zu der Karte
        when(tagRepMock.getTagsToCard(card1)).thenReturn(existingTags);
        //Manuell angelegte cardToTags für die zu löschenden Tags
        CardToTag cardToTag = new CardToTag(card1,exTag3);
        CardToTag cardToTag1 = new CardToTag(card1,exTag4);
        //Mocke das Repo, so dass diese beim Suchen zurückgegeben werden
        when(cardToTagRepMock.findSpecificCardToTag(card1,exTag3)).thenReturn(cardToTag);
        when(cardToTagRepMock.findSpecificCardToTag(card1,exTag4)).thenReturn(cardToTag1);
        //Tue nichts wenn die Card To tags gelöscht werden sollen
        doNothing().when(cardToTagRepMock).delete((any(CardToTag.class)));

        //test
        cardLogic.setTagsToCard(card1, tagsToAdd);

    }

    /**
     * Teste das Löschen und Hinzufügen von Tags zu einer Karte
     */
    @Test
    public void RemoveAndAddTagsToExistingCard() {
        //testdaten
        Tag exTag1 = new Tag("Erdkunde");
        Tag exTag2 = new Tag("Spanisch");
        ArrayList<Tag> existingTags = new ArrayList<>() {
            {
                add(exTag1);
                add(exTag2);
            }
        };

        Tag newTag1 = new Tag("Deutsch");
        Tag newTag2 = new Tag("Englisch");

        ArrayList<Tag> TagsToAdd = new ArrayList<>() {
            {
                add(exTag1);
                add(newTag1);
                add(newTag2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");

        //Bereits gespeicherte Tags zu der Karte
        when(tagRepMock.getTagsToCard(card1)).thenReturn(existingTags);
        //Zu löschendes manuell angelegtes Card To Tag, gib dieses bei Suche zurück
        CardToTag cardToTag = new CardToTag(card1,exTag2);
        when(cardToTagRepMock.findSpecificCardToTag(card1,exTag2)).thenReturn(cardToTag);
        //Tue nichts beim Löschen
        doNothing().when(cardToTagRepMock).delete((any(CardToTag.class)));
        //Neue Tags noch nicht in DB gespeichert, alte wird nicht gesucht
        when(tagRepMock.findTag(newTag1.getVal())).thenThrow(NoResultException.class);
        when(tagRepMock.findTag(newTag2.getVal())).thenThrow(NoResultException.class);
        //Tue nichts beim Speichern der neuen Tags und CardToTags
        when(tagRepMock.save(newTag1)).thenReturn(newTag1);
        when(tagRepMock.save(newTag2)).thenReturn(newTag2);
        CardToTag t1 = new CardToTag(card1,newTag1);
        CardToTag t2 = new CardToTag(card1,newTag2);
        when(cardToTagRepMock.save(new CardToTag(card1, newTag1))).thenReturn(t1);
        when(cardToTagRepMock.save(new CardToTag(card1, newTag2))).thenReturn(t2);

        //Test
        cardLogic.setTagsToCard(card1, TagsToAdd);
    }

}
