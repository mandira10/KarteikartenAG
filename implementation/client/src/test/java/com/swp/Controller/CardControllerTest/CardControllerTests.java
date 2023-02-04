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
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen mithilfe von Komponententests.
 * Einzig die beiden Funktionen für den nicht-trivialen Anwendungsfall  'N3-8' Karteikarte Schlagwörter (Tags) zuordnen
 * werden separat über White-Box-Tests dem Ordner "ThreeNonTrivialMethods" getestet.
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

    private DataCallback<CardOverview> coMockDataCallback;
    private SingleDataCallback<Boolean> coMockbSingleDataCallBack;
    private SingleDataCallback<Card> coMockCSingleDataCallBack;

    /**
     * Before-Each Tests Methode.
     * Die CardLogic wird gemockt und im Controller als gemockt gesetzt.
     * Die Sprachvariable wird auf Deutsch eingestellt.
     */
    @BeforeEach
    public void beforeEach(){
        cardMockLogic = mock(CardLogic.class);
        coMockDataCallback = mock(DataCallback.class);
        coMockbSingleDataCallBack = mock(SingleDataCallback.class);
        coMockCSingleDataCallBack = mock(SingleDataCallback.class);
        on(cardController).set("cardLogic",cardMockLogic);
    }

    /**
     * BeforeAll wird synchronizedTasks aufgerufen und die PU initialisiert für die Tests.
     */
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsToShow
     */
    @Test
    public void getCardsToShowTestWithList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsToShow(2, 3, coMockDataCallback));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsToShow
     */
    @Test
    public void getCardsToShowTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(list);
        assertDoesNotThrow(() ->     cardController.getCardsToShow(2, 3,coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardstoshowempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Exception geworfen wird.
     * Funktion: getCardsToShow
     */
    @Test
    public void getCardsToShowTestException(){
        when(cardMockLogic.getCardOverview(any(Integer.class),any(Integer.class))).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() ->  cardController.getCardsToShow(2, 3, coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardstoshowerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsToShowWithOrder
     */
    @Test
    public void getCardsToShowOrderTestWithList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardOverview(2,3,ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsToShow(2, 3, ListOrder.Order.DATE, false, coMockDataCallback));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsToShowWithOrder
     */
    @Test
    public void getCardsToShowOrderTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardOverview(2,3,ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsToShow(2, 3, ListOrder.Order.DATE, false, coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardstoshowempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Exception geworfen wird.
     * Funktion: getCardsToShowWithOrder
     */
    @Test
    public void getCardsToShowOrderTestException(){
        when(cardMockLogic.getCardOverview(2,3,ListOrder.Order.DATE, false)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> cardController.getCardsToShow(2, 3, ListOrder.Order.DATE, false, coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardstoshowerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsByTag
     */
    @Test
    public void getCardsByTagTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsByTag(any(String.class))).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsByTag
     */
    @Test
    public void getCardsByTagTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsByTag(any(String.class))).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardsbytagempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsByTag
     */
    @Test
    public void getCardsByTagTestNormalException(){
        when(cardMockLogic.getCardsByTag(any(String.class))).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardsbytagerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsByTag
     */
    @Test
    public void getCardsByTagTestIllegalArgumentException(){
        when(cardMockLogic.getCardsByTag("")).thenThrow( new IllegalArgumentException("nonempty"));
        assertDoesNotThrow(() -> cardController.getCardsByTag("",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure( "Schlagwort darf nicht leer sein!");
        reset(coMockDataCallback);
    }


    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsByTag with Order
     */
    @Test
    public void getCardsByTagOrderTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsByTag("test",ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsByTag Order
     */
    @Test
    public void getCardsByTagOrderTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsByTag("test",ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardsbytagempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsByTag Order
     */
    @Test
    public void getCardsByTagOrderTestNormalException(){
        when(cardMockLogic.getCardsByTag("test",ListOrder.Order.DATE, false)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> cardController.getCardsByTag("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardsbytagerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsByTag Order
     */
    @Test
    public void getCardsByTagOrderTestIllegalArgumentException(){
        when(cardMockLogic.getCardsByTag("",ListOrder.Order.DATE, false)).thenThrow( new IllegalArgumentException("nonempty"));
        assertDoesNotThrow(() -> cardController.getCardsByTag("",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure( "Schlagwort darf nicht leer sein!");
        reset(coMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsBySearchterms
     */
    @Test
    public void getCardsBySearchtermsTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsBySearchterms
     */
    @Test
    public void getCardsBySearchtermsTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardsbysearchtermsempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsBySearchterms
     */
    @Test
    public void getCardsBySearchtermsTestNormalException(){
        when(cardMockLogic.getCardsBySearchterms(any(String.class))).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardsbysearchtermserror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsBySearchterms
     */
    @Test
    public void getCardsBySearchtermsTestIllegalArgumentException(){
        when(cardMockLogic.getCardsBySearchterms("")).thenThrow( new IllegalArgumentException("nonempty"));
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure( "Suchbegriff darf nicht leer sein!");
        reset(coMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsBySearchterms with Order
     */
    @Test
    public void getCardsBySearchtermsOrderTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardMockLogic.getCardsBySearchterms("test",ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsBySearchterms Order
     */
    @Test
    public void getCardsBySearchtermsOrderTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(cardMockLogic.getCardsBySearchterms("test",ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardsbysearchtermsempty"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsBySearchterms Order
     */
    @Test
    public void getCardsBySearchtermsOrderTestNormalException(){
        when(cardMockLogic.getCardsBySearchterms("test",ListOrder.Order.DATE, false)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("test",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardsbysearchtermserror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsBySearchterms Order
     */
    @Test
    public void getCardsBySearchtermsOrderTestIllegalArgumentException(){
        when(cardMockLogic.getCardsBySearchterms("",ListOrder.Order.DATE, false)).thenThrow( new IllegalArgumentException("nonempty"));
        assertDoesNotThrow(() -> cardController.getCardsBySearchterms("",coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure( "Suchbegriff darf nicht leer sein!");
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn null übergeben wird.
     * Funktion: deleteCard
     */

    @Test
    public void deleteCardTestNull(){
        Card card = new TrueFalseCard();
        doThrow(new IllegalStateException("cardnullerror")).when(cardMockLogic).deleteCard(card);
        assertDoesNotThrow(() -> cardController.deleteCard(card, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Exception geworfen wird.
     * Funktion: deleteCard
     */
    @Test
    public void deleteCardTestException(){
        Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Test")).when(cardMockLogic).deleteCard(card);
        assertDoesNotThrow(() -> cardController.deleteCard(card, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("deletecarderror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion, beim Controller.
     * Funktion: deleteCard
     */
    @Test
    public void deleteCardTest(){
        Card card = new TrueFalseCard();
        doNothing().when(cardMockLogic).deleteCard(card);
        assertDoesNotThrow(() -> cardController.deleteCard(card, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }


        }));
        reset(coMockbSingleDataCallBack);

    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn null übergeben wird.
     * Funktion: deleteCards
     */
    @Test
    public void deleteCardsTestNull(){
        List list = Arrays.asList(new TextCard(), new TextCard());
        doThrow(new IllegalStateException("cardnullerror")).when(cardMockLogic).deleteCards(list);
        assertDoesNotThrow(() -> cardController.deleteCards(list, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird.
     * Funktion: deleteCards
     */
    @Test
    public void deleteCardsTestException(){
        List list = Arrays.asList(new TextCard(), new TextCard());
        doThrow(new RuntimeException("Test")).when(cardMockLogic).deleteCards(list);
        assertDoesNotThrow(() -> cardController.deleteCards(list, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("deletecardserror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion, beim Controller.
     * Funktion: deleteCards
     */
    @Test
    public void deletesCardTest(){
        List list = Arrays.asList(new TextCard(), new TextCard());
        doNothing().when(cardMockLogic).deleteCards(list);
        assertDoesNotThrow(() -> cardController.deleteCards(list, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }


        }));
        reset(coMockbSingleDataCallBack);

    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine NoResultException geworfen wird.
     * Funktion: getCardByUUID
     */
    @Test
    public void getCardByUUIDTestNoResultException(){
        when(cardMockLogic.getCardByUUID("Test")).thenThrow(new NoResultException("Es "));
        assertDoesNotThrow(() -> cardController.getCardByUUID("Test", coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure("Es konnte nichts gefunden werden.");
        reset(coMockCSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: getCardByUUID
     */
    @Test
    public void getCardByUUIDTestException(){
        when(cardMockLogic.getCardByUUID("Test")).thenThrow(new RuntimeException("test"));
        assertDoesNotThrow(() -> cardController.getCardByUUID("Test", coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardbyuuiderror"));
        reset(coMockCSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getCardByUUID
     */
    @Test
    public void getCardByUUID(){
        final Card[] card1 = {new MultipleChoiceCard()};
        when(cardMockLogic.getCardByUUID("Test")).thenReturn(card1[0]);
        assertDoesNotThrow(() -> cardController.getCardByUUID("Test", coMockCSingleDataCallBack));
            verify(coMockCSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Card>() {
                @Override
                public boolean matches(Card card) {
                     assertEquals(card1[0],card);
                     return true;
                }

            }));
            reset(coMockCSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn die UUID null ist
     * Funktion: getCardByUUID
     */
    @Test
    public void getCardByUUIDTestNullUUID(){
        when(cardMockLogic.getCardByUUID("Test")).thenThrow(new IllegalArgumentException("nonnull"));
        assertDoesNotThrow(() -> cardController.getCardByUUID("Test", coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure("ID darf nicht null sein!");
        reset(coMockCSingleDataCallBack);

    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: updateCardData
     */
    @Test
    public void updateCardDataException(){
        final Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Test")).when(cardMockLogic).updateCardData(card,true);
        assertDoesNotThrow(() -> cardController.updateCardData(card, true, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("updatecreatecarderror"));
        reset(coMockbSingleDataCallBack);

    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn die Karte null ist
     * Funktion: getCardByUUID
     */
    @Test
    public void updateCardDataNull(){
        final Card card = null;
        doThrow(new IllegalStateException("cardnullerror")).when(cardMockLogic).updateCardData(card,true);
        assertDoesNotThrow(() -> cardController.updateCardData(card, true, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        reset(coMockbSingleDataCallBack);
    }
    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: updateCardData
     */

    @Test
    public void updateCardData(){
        final Card card = new TrueFalseCard();
        doNothing().when(cardMockLogic).updateCardData(card,true);
        assertDoesNotThrow(() -> cardController.updateCardData(card,true, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }


        }));
        reset(coMockbSingleDataCallBack);
    }


    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Exception geworfen wird.
     * Funktion: exportCards
     */
        @Test
        public void exportCardsFalse(){
            final List<CardOverview> cards = Arrays.asList(new CardOverview(), new CardOverview());
            String filepath = "Test";
            when(cardMockLogic.exportCards(cards, filepath, Exporter.ExportFileType.EXPORT_XML)).thenThrow(new RuntimeException("Test"));
            assertDoesNotThrow(() -> cardController.exportCards(cards, filepath, Exporter.ExportFileType.EXPORT_XML, coMockbSingleDataCallBack));
            verify(coMockbSingleDataCallBack, times(1))
                    .callFailure(Locale.getCurrentLocale().getString("cardexporterror"));
            reset(coMockbSingleDataCallBack);
        }

    /**
     * Testet die callSuccess Funktion beim Controller.
     * Funktion: exportCards
     */
        @Test
        public void exportCards(){
            final List<CardOverview> cards = Arrays.asList(new CardOverview(), new CardOverview());
            String filepath = "Test";
            when(cardMockLogic.exportCards(cards,filepath, Exporter.ExportFileType.EXPORT_XML)).thenReturn(true);
            assertDoesNotThrow(() -> cardController.exportCards(cards, filepath, Exporter.ExportFileType.EXPORT_XML, coMockbSingleDataCallBack));
            verify(coMockbSingleDataCallBack, times(1))
                    .callSuccess(true);
        }



}
