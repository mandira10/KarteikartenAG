package com.swp.Controller.CategoryControllerTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.CategoryController;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.Language.German;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen mithilfe von Komponententests.
 * Einzig die beiden Funktionen für den nicht-trivialen Anwendungsfall  'N26 Karteikasten mehrere Kategorien'
 * werden separat über White-Box-Tests dem Ordner "ThreeNonTrivialMethods" getestet.
 */
public class CategoryControllerTests {

    private CategoryLogic categoryMockLogic;
    private final CategoryController categoryController = CategoryController.getInstance();

    private DataCallback<CardOverview> coMockDataCallback;
    private DataCallback<Category> catMockDataCallback;
    private SingleDataCallback<Boolean> coMockbSingleDataCallBack;
    private SingleDataCallback<Category> coMockCSingleDataCallBack;
    private SingleDataCallback<String> coMockSSingleDataCallBack;

    /**
     * Before-Each Tests Methode.
     * Die CategoryLogic wird gemockt und im Controller als gemockt gesetzt.
     */
    @BeforeEach
    public void beforeEach(){
        categoryMockLogic = mock(CategoryLogic.class);
        coMockDataCallback = mock(DataCallback.class);
        coMockbSingleDataCallBack = mock(SingleDataCallback.class);
        coMockCSingleDataCallBack = mock(SingleDataCallback.class);
        coMockSSingleDataCallBack = mock(SingleDataCallback.class);
        catMockDataCallback = mock(DataCallback.class);
        on(categoryController).set("categoryLogic",categoryMockLogic);
    }

    /**
     * BeforeAll wird synchronizedTasks aufgerufen und die PU initialisiert für die Tests,
     * sowie die Language Variable gesetzt.
     */
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: updateCategoryData
     */
    @Test
    public void updateCategoryDataExceptionTest(){
        final Category cat = new Category("Test");
        doThrow(new RuntimeException("Test")).when(categoryMockLogic).updateCategoryData(cat,true,false);
        assertDoesNotThrow(() -> categoryController.updateCategoryData(cat,true,false,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categoryupdatesaveerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn die Karte null ist
     * Funktion: updateCategoryData
     */
    @Test
    public void updateCategoryDataNullTest(){
        final Category cat = null;
        doThrow(new IllegalStateException("categorynullerror")).when(categoryMockLogic).updateCategoryData(cat,true,false);
        assertDoesNotThrow(() ->categoryController.updateCategoryData(cat, true,false,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorynullerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: updateCategoryData
     */
    @Test
    public void updateCategoryDataTest(){
        final Category cat = new Category("Test");
        doNothing().when(categoryMockLogic).updateCategoryData(cat,true,false);
        assertDoesNotThrow(() -> categoryController.updateCategoryData(cat,true, false, coMockbSingleDataCallBack));
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
     * Funktion: deleteCategory
     */
    @Test
    public void deleteCategoryTestNull(){
        Category cat =null;
        doThrow(new IllegalStateException("categorynullerror")).when(categoryMockLogic).deleteCategory(cat);
        assertDoesNotThrow(() ->   categoryController.deleteCategory(cat,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorynullerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller,wenn eine Exception geworfen wird.
     * Funktion: deleteCategory
     */
    @Test
    public void deleteCategoryTestException(){
        Category cat = new Category("Test");
        doThrow(new RuntimeException("test")).when(categoryMockLogic).deleteCategory(cat);
        assertDoesNotThrow(() -> categoryController.deleteCategory(cat, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("deletecategoryerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion, beim Controller.
     * Funktion: deleteCategory
     */
    @Test
    public void deleteCategoryTest(){
        Category cat = new Category("Test");
        doNothing().when(categoryMockLogic).deleteCategory(cat);

        assertDoesNotThrow(() ->  categoryController.deleteCategory(cat,coMockbSingleDataCallBack));
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
     * Funktion: deleteCategories
     */
    @Test
    public void deleteCategoriesTestNull(){
        List list = null;
        doThrow(new IllegalStateException("categoriesnullerror")).when(categoryMockLogic).deleteCategories(list);
        assertDoesNotThrow(() -> categoryController.deleteCategories(list, coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categoriesnullerror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird.
     * Funktion: deleteCategories
     */
    @Test
    public void deleteCategoriesTestException(){
        final List<Category> list = Arrays.asList(new Category("T"),new Category("C"));
        doThrow(new RuntimeException("Test")).when(categoryMockLogic).deleteCategories(list);

        assertDoesNotThrow(() -> categoryController.deleteCategories(list,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("deletecategorieserror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird.
     * Funktion: deleteCategories
     */
    @Test
    public void deleteCategoriesTest(){
        final List<Category> list = Arrays.asList(new Category("T"),new Category("C"));
        doNothing().when(categoryMockLogic).deleteCategories(null);

        assertDoesNotThrow(() ->  categoryController.deleteCategories(list,coMockbSingleDataCallBack));
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
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsInCategory
     */
    @Test
    public void getCardsInCategoryTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(categoryMockLogic.getCardsInCategory("Test")).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory("Test", coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardincategoryempty"));
        reset(coMockDataCallback);

    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsInCategory
     */
    @Test
    public void getCardsInCategoryTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(categoryMockLogic.getCardsInCategory("Test")).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory("Test",  coMockDataCallback));
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
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsInCategory
     */
    @Test
    public void getCardsInCategoryTestNormalException(){
        when(categoryMockLogic.getCardsInCategory("Test")).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> categoryController.getCardsInCategory("Test", coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsInCategory
     */
    @Test
    public void getCardsInCategoryTestIllegalArgumentException(){
        when(categoryMockLogic.getCardsInCategory("")).thenThrow(new IllegalStateException("nonempty"));
        assertDoesNotThrow(() ->categoryController.getCardsInCategory("",coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure( "Kategorie darf nicht leer sein!");
        reset(coMockDataCallback);
    }
    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsInCategory2
     */
    @Test
    public void getCardsInCategory2TestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory(cat, coMockDataCallback));
        reset(coMockDataCallback);

    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsInCategory2
     */
    @Test
    public void getCardsInCategory2TestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory(cat,  coMockDataCallback));
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
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsInCategory2
     */
    @Test
    public void getCardsInCategory2TestNormalException(){
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> categoryController.getCardsInCategory(cat, coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsInCategory2
     */
    @Test
    public void getCardsInCategory2TestNull(){
        Category cat = null;
        when(categoryMockLogic.getCardsInCategory(cat)).thenThrow(new IllegalStateException("categorynullerror"));
        assertDoesNotThrow(() ->categoryController.getCardsInCategory(cat,coMockDataCallback));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorynullerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCardsInCategory Order
     */
    @Test
    public void getCardsInCategoryOrderTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(categoryMockLogic.getCardsInCategory("Test", ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory("Test", coMockDataCallback,ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcardincategoryempty"));
        reset(coMockDataCallback);

    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCardsInCategory Order
     */
    @Test
    public void getCardsInCategoryOrderTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(categoryMockLogic.getCardsInCategory("Test", ListOrder.Order.DATE, false)).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCardsInCategory("Test",  coMockDataCallback, ListOrder.Order.DATE, false));
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
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCardsInCategory Order
     */
    @Test
    public void getCardsInCategoryOrderTestNormalException(){
        when(categoryMockLogic.getCardsInCategory("Test", ListOrder.Order.DATE, false)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> categoryController.getCardsInCategory("Test", coMockDataCallback, ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcardincategoryerror"));
        reset(coMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCardsInCategory Order
     */
    @Test
    public void getCardsInCategoryOrderTestIllegalArgumentException(){
        when(categoryMockLogic.getCardsInCategory("", ListOrder.Order.DATE, false)).thenThrow(new IllegalStateException("nonempty"));
        assertDoesNotThrow(() ->categoryController.getCardsInCategory("",coMockDataCallback, ListOrder.Order.DATE, false));
        verify(coMockDataCallback, times(1))
                .callFailure( "Kategorie darf nicht leer sein!");
        reset(coMockDataCallback);
    }


//    @Test
//    public void getCardsInCategoriesTestNullException(){
//        final List<Category> catLit = Arrays.asList(null, new Category("L"));
//        when(categoryMockLogic.getCardsInCategories(catLit)).thenThrow(new IllegalStateException("categorynullerror"));
//        final String expected = "Kategorie existiert nicht";
//        final String[] actual = new String[1];
//        categoryController.getCardsInCategories(catLit, new DataCallback<CardOverview>() {
//            @Override
//            public void onSuccess(List<CardOverview> data) {
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                actual[0] = msg;
//            }
//
//            @Override
//            public void onInfo(String msg) {
//            }
//        });
//        assertEquals(expected,actual[0]);
//    }


    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCategoriesByCard
     */
    @Test
    public void getCategoriesToCardTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getCategoriesByCard(card)).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getCategoriesToCard(card, catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {
            @Override
            public boolean matches(List<Category> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCategoriesByCard
     */
    @Test
    public void getCategoriesToCardTestNormalException(){
        Card card = null;
        when(categoryMockLogic.getCategoriesByCard(card)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() ->categoryController.getCategoriesToCard(card, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcategoriestocarderror"));
        reset(catMockDataCallback);

    }

    /**
     * Testet die Errormeldung, wenn null
     * Funktion: getChildrenForCategory
     */
    @Test
    public void getChildrenForCategoryNull(){
        final List<Category> list = new ArrayList<>();
          Category cat = null;
        when(categoryMockLogic.getChildrenForCategory(cat)).thenThrow(new IllegalStateException("categorynullerror"));
        assertDoesNotThrow(() -> categoryController.getChildrenForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorynullerror"));
        reset(catMockDataCallback);
    }


    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getChildrenForCategory
     */
    @Test
    public void getChildrenForCategoryTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenReturn(list);

        assertDoesNotThrow(() -> categoryController.getChildrenForCategory(cat, catMockDataCallback));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getChildrenForCategory
     */
    @Test
    public void getChildrenForCategoryTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getChildrenForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {
            @Override
            public boolean matches(List<Category> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller,eine Exception geworfen wird
     * Funktion: getChildrenForCategory
     */
    @Test
    public void getChildrenForCategoryTestNormalException(){
        Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenThrow(new RuntimeException("test"));
        assertDoesNotThrow(() ->  categoryController.getChildrenForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("catchilderror"));
        reset(catMockDataCallback);  
    }

    /**
     * Testet die Errormeldung, wenn null
     * Funktion: getParentsForCategory
     */
    @Test
    public void getParentsForCategoryNull(){
        Category cat = null;
        when(categoryMockLogic.getParentsForCategory(cat)).thenThrow(new IllegalStateException("categorynullerror"));
        assertDoesNotThrow(() -> categoryController.getParentsForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorynullerror"));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getParentsForCategory
     */
    @Test
    public void getParentsForCategoryTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        Category cat = new Category("T");
        when(categoryMockLogic.getParentsForCategory(cat)).thenReturn(list);

        assertDoesNotThrow(() -> categoryController.getParentsForCategory(cat, catMockDataCallback));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getParentsForCategory
     */
    @Test
    public void getParentsForCategoryTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        Category cat = new Category("T");
        when(categoryMockLogic.getParentsForCategory(cat)).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getParentsForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {
            @Override
            public boolean matches(List<Category> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird
     * Funktion: getParentsForCategory
     */
    @Test
    public void getParentsForCategoryTestNormalException(){
        Category cat = new Category("T");
        when(categoryMockLogic.getParentsForCategory(cat)).thenThrow(new RuntimeException("test"));
        assertDoesNotThrow(() ->  categoryController.getParentsForCategory(cat, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("catparenterror"));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn die Rückgabe leer ist.
     * Funktion: getCategories
     */
    @Test
    public void getCategoriesTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        when(categoryMockLogic.getCategories()).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCategories(catMockDataCallback));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getCategories
     */
    @Test
    public void getCategoriesTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category());
        when(categoryMockLogic.getCategories()).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getCategories(catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {
            @Override
            public boolean matches(List<Category> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird
     * Funktion: getCategories
     */
    @Test
    public void getCategoriesTestNormalException(){
        when(categoryMockLogic.getCategories()).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() ->categoryController.getCategories(catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcatorieserror"));
        reset(catMockDataCallback);
    }


    /**
     * Testet die callInfo Funktion beim Controller, wenn die Rückgabe leer ist.
     * Funktion: getRootCategories
     */
    @Test
    public void getRootCategoriesTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        when(categoryMockLogic.getRootCategories(false)).thenReturn(list);
        assertDoesNotThrow(() ->  categoryController.getRootCategories(false,catMockDataCallback));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getRootCategories
     */
    @Test
    public void getRootCategoriesTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        when(categoryMockLogic.getRootCategories(false)).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getRootCategories(false, catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {
            @Override
            public boolean matches(List<Category> overviews) {
                overviews.equals(list);
                return true;
            }

        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, eine Exception geworfen wird
     * Funktion: getRootCategories
     */
    @Test
    public void getRootCategoriesTestNormalException(){
        when(categoryMockLogic.getRootCategories(false)).thenThrow(new RuntimeException("te"));
        assertDoesNotThrow(() ->categoryController.getRootCategories(false, catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getrootcategorieserror"));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine NoResultException geworfen wird.
     * Funktion: getCategoryByUUID
     */
    @Test
    public void getCategoryByUUIDTestNoResultException(){
        when(categoryMockLogic.getCategoryByUUID("Test")).thenThrow(new NoResultException("Test"));
        assertDoesNotThrow(() -> categoryController.getCategoryByUUID("Test",  coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure("Es konnte nichts gefunden werden.");
        reset(coMockCSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: getCategoryByUUID
     */
    @Test
    public void getCategoryByUUIDTestException(){
        when(categoryMockLogic.getCategoryByUUID("Test")).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> categoryController.getCategoryByUUID("Test",  coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categorybyuuiderror"));
        reset(coMockCSingleDataCallBack);

    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: getCategoryByUUID
     */
    @Test
    public void getCategoryByUUID(){
        final Category category1 = new Category("T");
        when(categoryMockLogic.getCategoryByUUID("Test")).thenReturn(category1);
        assertDoesNotThrow(() -> categoryController.getCategoryByUUID("Test", coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Category>() {
            @Override
            public boolean matches(Category category) {
                assertEquals(category1,category);
                return true;
            }

        }));
        reset(coMockCSingleDataCallBack);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn die UUID null ist
     * Funktion: getCategoryByUUID
     */
    @Test
    public void getCategoryByUUIDTestNullUUID(){
        when(categoryMockLogic.getCategoryByUUID("Test")).thenThrow(new IllegalArgumentException("nonnull"));
        assertDoesNotThrow(() ->   categoryController.getCategoryByUUID("Test",  coMockCSingleDataCallBack));
        verify(coMockCSingleDataCallBack, times(1))
                .callFailure("ID darf nicht null sein!");
        reset(coMockCSingleDataCallBack);
    }


    /**
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: setCardToCategories
     */
    @Test
    public void setCategoriesToCardTestException(){
        final Card card = null;
        final List<Category> list = Arrays.asList(new Category("Test"), new Category("Test1"), new Category("Test3"));
        doThrow(new RuntimeException("Test")).when(categoryMockLogic).setCardToCategories(card,list);
        assertDoesNotThrow(() ->categoryController.setCategoriesToCard(card,list,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("setcategoriestocarderror"));
        reset(coMockbSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion beim Controller
     * Funktion: setCategoriesToCard
     */
        @Test
        public void setCategoriesToCardTest(){
            final Card card = new TrueFalseCard();
            final List<Category> list = Arrays.asList(new Category("Test"), new Category("Test1"), new Category("Test3"));
            doNothing().when(categoryMockLogic).setCardToCategories(card,list);
            assertDoesNotThrow(() ->  categoryController.setCategoriesToCard(card,list, coMockbSingleDataCallBack));
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
     * Testet die callFailure Funktion beim Controller, wenn eine Runtime Exception geworfen wird.
     * Funktion: editCategoryHierarchy
     */
    @Test
    public void editCategoryHierarchyExceptionTest(){
        final Category cat = new Category("Test");
        final List<Category> parents = Arrays.asList(new Category("Parent1"),new Category("Parent2"));
        final List<Category> childs = Arrays.asList(new Category("Childs1"),new Category("Childs2"));
        doThrow(new RuntimeException("Test")).when(categoryMockLogic).editCategoryHierarchy(
                cat,parents,childs);
        assertDoesNotThrow(() -> categoryController.editCategoryHierarchy(cat,parents,childs, coMockSSingleDataCallBack));
        verify(coMockSSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("categoryhierarchyupdateerror"));
        reset(coMockSSingleDataCallBack);
    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn es keine selfreference gibt
     * Funktion: editCategoryHierarchy
     */
    @Test
    public void editCategoryHierarchyTest(){
        final Category cat = new Category("Test");
        final List<Category> parents = Arrays.asList(new Category("Parent1"),new Category("Parent2"));
        final List<Category> childs = Arrays.asList(new Category("Childs1"),new Category("Childs2"));
        when(categoryMockLogic.editCategoryHierarchy(
                cat,parents,childs)).thenReturn(false);
        assertDoesNotThrow(() -> categoryController.editCategoryHierarchy(cat,parents,childs,coMockSSingleDataCallBack));
        verify(coMockSSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String s) {
                assertEquals("",s);
                return true;
            }

        }));
        reset(coMockSSingleDataCallBack);

    }
    /**
     * Testet die callSuccess Funktion beim Controller, wenn es eine selfreference gibt
     * Funktion: editCategoryHierarchy
     */
    @Test
    public void editCategoryHierarchySelfReferenceTest(){
        final Category cat = new Category("Test");
        final List<Category> parents = Arrays.asList(new Category("Parent1"),new Category("Parent2"));
        final List<Category> childs = Arrays.asList(new Category("Childs1"),new Category("Childs2"));
        when(categoryMockLogic.editCategoryHierarchy(
                cat,parents,childs)).thenReturn(true);
        assertDoesNotThrow(() -> categoryController.editCategoryHierarchy(cat,parents,childs,coMockSSingleDataCallBack));
        verify(coMockSSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String s) {
                assertEquals("Kategorie darf nicht selbstreferenziert oder gleiche Parents wie Childs haben! Nicht hinzugefügt!",s);
                return true;
            }

        }));
        reset(coMockSSingleDataCallBack);

    }

    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: getCategoriesBySearchterms
     */
    @Test
    public void getCategoriesBySearchtermsTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category());
        when(categoryMockLogic.getCategoriesBySearchterms("test")).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getCategoriesBySearchterm("test",catMockDataCallback));
        verify(catMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<Category>>() {

            @Override
            public boolean matches(List<Category> categories) {
               assertEquals(categories,list);
               return true;
            }
        }));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callInfo Funktion beim Controller, wenn ein empty set zurückgegeben wird.
     * Funktion: getCategoriesBySearchterms
     */
    @Test
    public void getCategoriesBySearchtermsTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        when(categoryMockLogic.getCategoriesBySearchterms("test")).thenReturn(list);
        assertDoesNotThrow(() -> categoryController.getCategoriesBySearchterm("test",catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callInfo(Locale.getCurrentLocale().getString("getcateoriesbysearchtermsempty"));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: getCategoriesBySearchterms
     */
    @Test
    public void getCategoriesBySearchtermsTestNormalException(){
        when(categoryMockLogic.getCategoriesBySearchterms("test")).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> categoryController.getCategoriesBySearchterm("test",catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getcateoriesbysearchtermserror"));
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein leerer String übergeben wird.
     * Funktion: getCategoriesBySearchterms
     */
    @Test
    public void getCategoriesBySearchtermsTestIllegalArgumentException(){
        when(categoryMockLogic.getCategoriesBySearchterms("")).thenThrow( new IllegalArgumentException("nonempty"));
        assertDoesNotThrow(() -> categoryController.getCategoriesBySearchterm("",catMockDataCallback));
        verify(catMockDataCallback, times(1))
                .callFailure( "Suchbegriff darf nicht leer sein!");
        reset(catMockDataCallback);
    }

    /**
     * Testet die callFailure Funktion beim Controller, wenn ein random Exception geworfen und gefangen wird.
     * Funktion: removeCardsFromStudySystem
     */
    @Test
    public void removeCardsFromCategoryTestNormalException(){
        List<CardOverview> list = Arrays.asList(new CardOverview(),new CardOverview());
        Category cat = null;
        doThrow(new RuntimeException("Test")).when(categoryMockLogic).removeCardsFromCategory(list,cat);
        assertDoesNotThrow(() -> categoryController.removeCardsFromCategory(list,cat,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("removecardsfromstudysystemerror"));
        reset(coMockbSingleDataCallBack);
    }
    /**
     * Testet die callSuccess Funktion beim Controller, wenn ein Liste zurückgegeben wird.
     * Funktion: removeCardsFromCategory
     */
    @Test
    public void removeCardsFromCategoryTest(){
        List<CardOverview> list = Arrays.asList(new CardOverview(),new CardOverview());
        Category cat = new Category("test");
        doNothing().when(categoryMockLogic).removeCardsFromCategory(list,cat);
        assertDoesNotThrow(() -> categoryController.removeCardsFromCategory(list,cat,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean booleans) {
               assertTrue(booleans);
               return true;
            }

        }));
        reset(coMockbSingleDataCallBack);
    }
    /**
     * Testet die callInfo Funktion beim Controller, wenn die Rückgabe leer ist.
     * Funktion: removeCardsFromCategory
     */
    @Test
    public void removeCardsFromCategoryTestCardEmpty(){
        List<CardOverview> list = Arrays.asList(new CardOverview(),new CardOverview());
        Category cat = new Category("test");
        doThrow(new IllegalStateException("cardnullerror")).when(categoryMockLogic).removeCardsFromCategory(list,cat);
        assertDoesNotThrow(() -> categoryController.removeCardsFromCategory(list,cat,coMockbSingleDataCallBack));
        verify(coMockbSingleDataCallBack, times(1))
                .callFailure(Locale.getCurrentLocale().getString("cardnullerror"));
        reset(coMockbSingleDataCallBack);
    }


}
