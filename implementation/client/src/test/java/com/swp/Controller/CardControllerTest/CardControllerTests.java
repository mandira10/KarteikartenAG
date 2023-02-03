package com.swp.Controller.CardControllerTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.CardController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen mithilfe von Komponententests.
 */
public class CardControllerTests {

    /**
     * CardController Instanz
     */
    private CardController cardController = CardController.getInstance();

    /**
     * Gemockte CardLogic Instanz
     */
    private CardLogic cardMockLogic;

    /**
     * Attribute für die Spracheinstellung
     */
    private Locale locale = new Locale("German", "de");
    private int i;

    /**
     * Before-Each Tests Methode.
     * Die CardLogic wird gemockt und im Controller als gemockt gesetzt.
     * Die Sprachvariable wird auf Deutsch eingestellt.
     */
    @BeforeEach
    public void beforeEach(){
        cardMockLogic = mock(CardLogic.class);
        on(cardController).set("cardLogic",cardMockLogic);
    }

    /**
     * BeforeAll wird synchronizedTasks aufgerufen.
     */
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    @Test
    public void getCardsToShowTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(list);
        final String expected = "Es gibt bisher noch keine Karten.";
        final String[] actual = new String[1];
              cardController.getCardsToShow(2, 3, new DataCallback<CardOverview>() {
                  @Override
                  public void onSuccess(List<CardOverview> data) {
                  }

                  @Override
                  public void onFailure(String msg) {
                  }

                  @Override
                  public void onInfo(String msg) {
                      actual[0] = msg;
                  }
              });
        assertEquals(expected,actual[0]);

    }

    @Test
    public void getCardsToShowTestWithList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        cardController.getCardsToShow(2, 3, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getCardsToShowTestException(){
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenThrow(new RuntimeException("Test"));
        final String expected = "Beim Abrufen der Karten ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.getCardsToShow(2, 3, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);

    }



    @Test
    public void getCardsByTagTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsByTag(any(String.class))).thenReturn(list);
        final String expected = "Es gibt keine Karten für dieses Schlagwort.";
        final String[] actual = new String[1];
        cardController.getCardsByTag("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardsByTagTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsByTag(any(String.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        cardController.getCardsByTag("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getCardsByTagTestNormalException(){
        when(cardMockLogic.getCardsByTag(any(String.class))).thenThrow(new RuntimeException("Test"));
        final String expected = "Beim Suchen nach Karten für das Schlagwort ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.getCardsByTag("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardsByTagTestIllegalArgumentException(){
        when(cardMockLogic.getCardsByTag("")).thenThrow( new IllegalArgumentException("nonempty"));
        final String expected = "Schlagwort darf nicht leer sein!";
        final String[] actual = new String[1];
        cardController.getCardsByTag("", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getTagsToCardTestEmptySet(){
        final List<Tag> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        when(cardMockLogic.getTagsToCard(card)).thenReturn(list);
        final String[] actual = new String[1];
        cardController.getTagsToCard(card, new DataCallback<Tag>() {
            @Override
            public void onSuccess(List<Tag> data) {
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });
        assertNull(actual[0]);
    }

    @Test
    public void getTagsToCardTestWithReturningList(){
        final List<Tag> list = Arrays.asList(new Tag("tag1"), new Tag("tag2"));
        Card card = new TrueFalseCard();
        when(cardMockLogic.getTagsToCard(card)).thenReturn(list);
        final List<Tag> expected = list;
        final List<Tag>[] actual = new List[1];
        cardController.getTagsToCard(card, new DataCallback<Tag>() {
            @Override
            public void onSuccess(List<Tag> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getTagsToCardTestNormalException(){
        Card card = new TrueFalseCard();
        when(cardMockLogic.getTagsToCard(card)).thenThrow(new RuntimeException("Test"));
        final String expected = "Beim Suchen nach Schlagwörtern für die Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.getTagsToCard(card, new DataCallback<Tag>() {
            @Override
            public void onSuccess(List<Tag> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardsBySearchtermsTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenReturn(list);
        final String expected = "Keine Karten für das Suchwort gefunden.";
        final String[] actual = new String[1];
        cardController.getCardsBySearchterms("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardsBySearchtermsTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        cardController.getCardsBySearchterms("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getCardsBySearchtermsTestNormalException(){
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenThrow(new RuntimeException("Test"));
        final String expected = "Beim Suchen nach Karten mit dem Suchbegriff ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.getCardsBySearchterms("Test", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardsBySearchtermsTestIllegalArgumentException(){
        when(cardMockLogic.getCardsBySearchterms("")).thenThrow(new IllegalArgumentException("nonempty"));
        final String expected = "Suchbegriff darf nicht leer sein!";
        final String[] actual = new String[1];
        cardController.getCardsBySearchterms("", new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);
    }


    @Test
    public void deleteCardTestNull(){
        Card card = new TrueFalseCard();
        doThrow(new IllegalStateException("cardnullerror")).when(cardMockLogic).deleteCard(card);
        String expected = "Karte existiert nicht";
        final String[] actual = new String[1];
        cardController.deleteCard(card, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteCardTestException(){
        Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Test")).when(cardMockLogic).deleteCard(card);
        String expected = "Beim Löschen der Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.deleteCard(card, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteCardTest(){
        Card card = new TrueFalseCard();
        doNothing().when(cardMockLogic).deleteCard(card);

          cardController.deleteCard(card, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean  data) {
                }

                @Override
                public void onFailure(String msg) {
                }
        });

    }

    //TODO fix
//    @Test
//    public void deleteCardsTestNull() {
//        final Card card = null;
//        List<Card> cards = new ArrayList<>();
//        cards.add(card);
//        doThrow(new IllegalStateException("Karte existiert nicht")).when(cardMockLogic).deleteCard(card);
//        String expected = "Karte existiert nicht";
//        final String[] actual = new String[1];
//        cardController.deleteCards(cards, new SingleDataCallback<Boolean>() {
//            @Override
//            public void onSuccess(Boolean data) {
//
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                actual[0] = msg;
//            }
//
//        });
//        assertEquals(expected, actual[0]);
//    }


        @Test
    public void deleteCardsTestException(){
            final List<CardOverview> list = Arrays.asList(new CardOverview(),new CardOverview());
            doThrow(new RuntimeException("test")).when(cardMockLogic).deleteCards(list);
            String expected = "Beim Löschen der Karten ist ein Fehler aufgetreten.";
            final String[] actual = new String[1];
            cardController.deleteCards(list, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean  data) {

                }

                @Override
                public void onFailure(String msg) {
                    actual[0] = msg;
                }

            });
            assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteCardsTest(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        doNothing().when(cardMockLogic).deleteCards(null);

        cardController.deleteCards(list, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }




    @Test
    public void getCardByUUIDTestNoResultException(){
        when(cardMockLogic.getCardByUUID(any(String.class))).thenThrow(new NoResultException("Es "));
        final String expected = "Es konnte nichts gefunden werden.";
        final String[] actual = new String[1];
        cardController.getCardByUUID("Test", new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

          
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardByUUIDTestException(){
        when(cardMockLogic.getCardByUUID(any(String.class))).thenThrow(new RuntimeException("test"));
        final String expected = "Beim Abrufen der Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.getCardByUUID("Test", new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCardByUUID(){
        final Card[] card = {new MultipleChoiceCard()};
        when(cardMockLogic.getCardByUUID(any(String.class))).thenReturn(card[0]);
        final Card[] card1 = new Card[1];
        cardController.getCardByUUID("Test", new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
               card1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(card[0], card1[0]);
    }

    @Test
    public void getCardByUUIDTestNullUUID(){
        when(cardMockLogic.getCardByUUID(any(String.class))).thenThrow(new IllegalArgumentException("nonnull"));
        final String expected = "ID darf nicht null sein!";
        final String[] actual = new String[1];
        cardController.getCardByUUID("Test", new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }



    @Test
    public void setTagsToCardTestException(){
        final Card card = new TrueFalseCard();
        final List<Tag> list = Arrays.asList(new Tag("Test"), new Tag("Test1"), new Tag("Test3"));
        doThrow(new RuntimeException("Test")).when(cardMockLogic).setTagsToCard(card,list);
        String expected = "Beim Hinzufügen der Schlagwörter zu der Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        cardController.setTagsToCard(card,list, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }


        @Test
        public void setTagsToCardTest(){
            final Card card = new TrueFalseCard();
            final List<Tag> list = Arrays.asList(new Tag("Test"), new Tag("Test1"), new Tag("Test3"));
            doNothing().when(cardMockLogic).setTagsToCard(card,list);
            cardController.setTagsToCard(card,list, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean  data) {

                }

                @Override
                public void onFailure(String msg) {
                }

            });
        }

    @Test
    public void updateCardDataException(){
        final Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Test")).when(cardMockLogic).updateCardData(card,true);
        String expected = "Karte konnte nicht gespeichert oder geupdatet werden.";
        final String[] actual = new String[1];
        cardController.updateCardData(card,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }
    @Test
    public void updateCardDataNull(){
        final Card card = null;
        doThrow(new IllegalStateException("cardnullerror")).when(cardMockLogic).updateCardData(card,true);
        String expected = "Karte existiert nicht";
        final String[] actual = new String[1];
        cardController.updateCardData(card, true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }


    @Test
    public void updateCardData(){
        final Card card = new TrueFalseCard();
        doNothing().when(cardMockLogic).updateCardData(card,true);
        cardController.updateCardData(card,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
            }

        });
    }




        @Test
        public void exportCardsFalse(){
            final List<CardOverview> cards = Arrays.asList(new CardOverview(), new CardOverview());
            String filepath = "Test";
            when(cardMockLogic.exportCards(cards, filepath, Exporter.ExportFileType.EXPORT_XML)).thenThrow(new RuntimeException("Test"));
            String expected = "Es gab Probleme beim Exportieren der Karten.";
            final String[] actual = new String[1];
            cardController.exportCards(cards,filepath, Exporter.ExportFileType.EXPORT_XML, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {

                }

                @Override
                public void onFailure(String msg) {
                    actual[0] = msg;
                }

            });
            assertEquals(expected, actual[0]);
        }


        @Test
        public void exportCards(){
            final List<CardOverview> cards = Arrays.asList(new CardOverview(), new CardOverview());
            String filepath = "Test";
            when(cardMockLogic.exportCards(cards,filepath, Exporter.ExportFileType.EXPORT_XML)).thenReturn(true);
            cardController.exportCards(cards,filepath, Exporter.ExportFileType.EXPORT_XML, new SingleDataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean  data) {

                }

                @Override
                public void onFailure(String msg) {
                }

            });
        }



}
