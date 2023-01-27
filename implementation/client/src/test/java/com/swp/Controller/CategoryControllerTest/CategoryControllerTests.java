package com.swp.Controller.CategoryControllerTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.Logic.CategoryLogic;
import jakarta.persistence.NoResultException;
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
 * Testet die Controller Funktionen
 */
public class CategoryControllerTests {

    private CategoryLogic categoryMockLogic;
    private final CategoryController categoryController = CategoryController.getInstance();
    private Locale locale = new Locale("German", "de");
    private int i;
    @BeforeEach
    public void beforeEach(){
        categoryMockLogic = mock(CategoryLogic.class);
        on(categoryController).set("categoryLogic",categoryMockLogic);

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


    @Test
    public void updateCategoryDataExceptionTest(){
        final Category cat = new Category("Test");
        doThrow(new RuntimeException()).when(categoryMockLogic).updateCategoryData(cat,true,false);
        String expected = "Kategorie konnte nicht gespeichert oder geupdatet werden.";
        final String[] actual = new String[1];
        categoryController.updateCategoryData(cat,true,false, new SingleDataCallback<>() {
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
    public void updateCategoryDataNullTest(){
        final Category cat = null;
        doThrow(new IllegalStateException("Kategorie existiert nicht")).when(categoryMockLogic).updateCategoryData(cat,true,false);
        String expected = "Kategorie existiert nicht";
        final String[] actual = new String[1];
        categoryController.updateCategoryData(cat, true,false, new SingleDataCallback<>() {
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
    public void updateCategoryDataTest(){
        final Category cat = new Category("Test");
        doNothing().when(categoryMockLogic).updateCategoryData(cat,true,false);
        categoryController.updateCategoryData(cat,true, false, new SingleDataCallback<>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
            }

        });
    }

  
    @Test
    public void deleteCategoryTestNull(){
        Category cat = new Category("Test");
        doThrow(new IllegalStateException("Kategorie existiert nicht")).when(categoryMockLogic).deleteCategory(cat);
        String expected = "Kategorie existiert nicht";
        final String[] actual = new String[1];
        categoryController.deleteCategory(cat, new SingleDataCallback<>() {
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
    public void deleteCategoryTestException(){
        Category cat = new Category("Test");
        doThrow(new RuntimeException()).when(categoryMockLogic).deleteCategory(cat);
        String expected = "Beim Löschen der Kategorie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.deleteCategory(cat, new SingleDataCallback<>() {
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
    public void deleteCategoryTest(){
        Category cat = new Category("Test");
        doNothing().when(categoryMockLogic).deleteCategory(cat);

        categoryController.deleteCategory(cat, new SingleDataCallback<>() {
            @Override
            public void onSuccess(Boolean data) {
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
    public void deleteCategoriesTestException(){
        final List<Category> list = Arrays.asList(new Category("T"),new Category("C"));
        doThrow(new RuntimeException()).when(categoryMockLogic).deleteCategories(list);
        String expected = "Beim Löschen der Kategorien ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.deleteCategories(list, new SingleDataCallback<>() {
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
    public void deleteCategoriesTest(){
        final List<Category> list = Arrays.asList(new Category("T"),new Category("C"));
        doNothing().when(categoryMockLogic).deleteCategories(null);

        categoryController.deleteCategories(list, new SingleDataCallback<>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }



    @Test
    public void getCardsInCategoryTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        when(categoryMockLogic.getCardsInCategory(any(String.class))).thenReturn(list);
        final String expected = "Es gibt keine Karten zu dieser Kategorie.";
        final String[] actual = new String[1];
        categoryController.getCardsInCategory("Test", new DataCallback<>() {
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
    public void getCardsInCategoryTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(categoryMockLogic.getCardsInCategory(any(String.class))).thenReturn(list);
        final List[] actual = new List[1];
        categoryController.getCardsInCategory("Test", new DataCallback<>() {
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
        assertEquals(list, actual[0]);
    }

    @Test
    public void getCardsInCategoryTestNormalException(){
        when(categoryMockLogic.getCardsInCategory(any(String.class))).thenThrow(RuntimeException.class);
        final String expected = "Beim Suchen nach Karten für die Kategorie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCardsInCategory("Test", new DataCallback<>() {
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
    public void getCardsInCategoryTestIllegalArgumentException(){
        when(categoryMockLogic.getCardsInCategory("")).thenThrow(new IllegalStateException("Kategorie darf nicht leer sein!"));
        final String expected = "Kategorie darf nicht leer sein!";
        final String[] actual = new String[1];
        categoryController.getCardsInCategory("", new DataCallback<>() {
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
    public void getCardsInCategory2TestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenReturn(list);
        final String[] actual = new String[1];
        categoryController.getCardsInCategory(cat, new DataCallback<CardOverview>() {
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
        assertNull(actual[0]);
    }

    @Test
    public void getCardsInCategory2TestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        categoryController.getCardsInCategory(cat, new DataCallback<CardOverview>() {
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
    public void getCardsInCategory2TestNormalException(){
        Category cat = new Category("Test");
        when(categoryMockLogic.getCardsInCategory(cat)).thenThrow(RuntimeException.class);
        final String expected = "Beim Suchen nach Karten für die Kategorie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCardsInCategory(cat, new DataCallback<CardOverview>() {
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
    public void getCardsInCategory2TestNullException(){
        Category cat = null;
        when(categoryMockLogic.getCardsInCategory(cat)).thenThrow(new IllegalStateException(String.format("Kategorie existiert nicht")));
        final String expected = "Kategorie existiert nicht";
        final String[] actual = new String[1];
        categoryController.getCardsInCategory(cat, new DataCallback<CardOverview>() {
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
    public void getCardsInCategoriesTestEmptySet(){
        final List<CardOverview> list = new ArrayList<>();
        final List<Category> catLit = Arrays.asList(new Category("T"), new Category("L"));
        when(categoryMockLogic.getCardsInCategories(catLit)).thenReturn(list);
        final String expected = "Es gibt keine Karten für diese Kategorien.";
        final String[] actual = new String[1];
        categoryController.getCardsInCategories(catLit, new DataCallback<CardOverview>() {
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
    public void getCardsInCategoriesTestWithReturningList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        final List<Category> catLit = Arrays.asList(new Category("T"), new Category("L"));
        when(categoryMockLogic.getCardsInCategories(catLit)).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        categoryController.getCardsInCategories(catLit, new DataCallback<CardOverview>() {
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
    public void getCardsInCategoriesTestNormalException(){
        final List<Category> catLit = Arrays.asList(new Category("T"), new Category("L"));
        when(categoryMockLogic.getCardsInCategories(catLit)).thenThrow(RuntimeException.class);
        final String expected = "Beim Suchen nach Karten für die Kategorien ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCardsInCategories(catLit, new DataCallback<CardOverview>() {
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
    public void getCardsInCategoriesTestNullException(){
        final List<Category> catLit = Arrays.asList(null, new Category("L"));
        when(categoryMockLogic.getCardsInCategories(catLit)).thenThrow(new IllegalStateException(String.format("Kategorie existiert nicht")));
        final String expected = "Kategorie existiert nicht";
        final String[] actual = new String[1];
        categoryController.getCardsInCategories(catLit, new DataCallback<CardOverview>() {
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
    public void getCategoriesToCardTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getCategoriesByCard(card)).thenReturn(list);
        final String[] actual = new String[1];
        categoryController.getCategoriesToCard(card, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoriesToCardTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getCategoriesByCard(card)).thenReturn(list);
        final List<Category> expected = list;
        final List<Category>[] actual = new List[1];
        categoryController.getCategoriesToCard(card, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoriesToCardTestNormalException(){
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getCategoriesByCard(card)).thenThrow(RuntimeException.class);
        final String expected = "Beim Suchen nach Kategorien für die Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCategoriesToCard(card, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getChildrenForCategoryTestEmptySet(){
        final List<Category> list = new ArrayList<>();
          Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenReturn(list);
        final String[] actual = new String[1];
        categoryController.getChildrenForCategory(cat, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getChildrenForCategoryTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenReturn(list);
        final List<Category> expected = list;
        final List<Category>[] actual = new List[1];
        categoryController.getChildrenForCategory(cat, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getChildrenForCategoryTestNormalException(){
        Category cat = new Category("T");
        when(categoryMockLogic.getChildrenForCategory(cat)).thenThrow(RuntimeException.class);
        final String expected = "Beim Abrufen der Children für die Kategorie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getChildrenForCategory(cat, new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoriesTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        when(categoryMockLogic.getCategories()).thenReturn(list);
        final String[] actual = new String[1];
        categoryController.getCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoriesTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        when(categoryMockLogic.getCategories()).thenReturn(list);
        final List<Category> expected = list;
        final List<Category>[] actual = new List[1];
        categoryController.getCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoriesTestNormalException(){
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getCategories()).thenThrow(RuntimeException.class);
        final String expected = "Beim Suchen nach Kategorien ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getRootCategoriesTestEmptySet(){
        final List<Category> list = new ArrayList<>();
        when(categoryMockLogic.getRootCategories()).thenReturn(list);
        final String[] actual = new String[1];
        categoryController.getRootCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getRootCategoriesTestWithReturningList(){
        final List<Category> list = Arrays.asList(new Category("tag1"), new Category("tag2"));
        when(categoryMockLogic.getRootCategories()).thenReturn(list);
        final List<Category> expected = list;
        final List<Category>[] actual = new List[1];
        categoryController.getRootCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getRootCategoriesTestNormalException(){
        Card card = new TrueFalseCard();
        when(categoryMockLogic.getRootCategories()).thenThrow(RuntimeException.class);
        final String expected = "Beim Abrufen der KategorieHierarchie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getRootCategories(new DataCallback<Category>() {
            @Override
            public void onSuccess(List<Category> data) {
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
    public void getCategoryByUUIDTestNoResultException(){
        when(categoryMockLogic.getCategoryByUUID(any(String.class))).thenThrow(new NoResultException());
        final String expected = "Es konnte keine Kategorie zur UUID gefunden werden.";
        final String[] actual = new String[1];
        categoryController.getCategoryByUUID("Test", new SingleDataCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

          
        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCategoryByUUIDTestException(){
        when(categoryMockLogic.getCategoryByUUID(any(String.class))).thenThrow(new RuntimeException());
        final String expected = "Beim Abrufen der Kategorie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.getCategoryByUUID("Test", new SingleDataCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getCategoryByUUID(){
        final Category[] category = {new Category("T")};
        when(categoryMockLogic.getCategoryByUUID(any(String.class))).thenReturn(category[0]);
        final Category[] category1 = new Category[1];
        categoryController.getCategoryByUUID("Test", new SingleDataCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
               category1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(category[0], category1[0]);
    }

    @Test
    public void getCategoryByUUIDTestNullUUID(){
        when(categoryMockLogic.getCategoryByUUID(any(String.class))).thenThrow(new IllegalArgumentException("UUID darf nicht null sein"));
        final String expected = "UUID darf nicht null sein";
        final String[] actual = new String[1];
        categoryController.getCategoryByUUID("Test", new SingleDataCallback<Category>() {
            @Override
            public void onSuccess(Category data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }



    @Test
    public void setCategoriesToCardTestException(){
        final Card card = new TrueFalseCard();
        final List<Category> list = Arrays.asList(new Category("Test"), new Category("Test1"), new Category("Test3"));
        doThrow(new RuntimeException()).when(categoryMockLogic).setCardToCategories(card,list);
        String expected = "Beim Hinzufügen der Kategorien zu der Karte ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.setCategoriesToCard(card,list, new SingleDataCallback<>() {
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
        public void setCategoriesToCardTest(){
            final Card card = new TrueFalseCard();
            final List<Category> list = Arrays.asList(new Category("Test"), new Category("Test1"), new Category("Test3"));
            doNothing().when(categoryMockLogic).setCardToCategories(card,list);
            categoryController.setCategoriesToCard(card,list, new SingleDataCallback<>() {
                @Override
                public void onSuccess(Boolean data) {

                }

                @Override
                public void onFailure(String msg) {
                }

            });
        }

    @Test
    public void editCategoryHierarchyExceptionTest(){
        final Category cat = new Category("Test");
        final List<Category> parents = Arrays.asList(new Category("Parent1"),new Category("Parent2"));
        final List<Category> childs = Arrays.asList(new Category("Childs1"),new Category("Childs2"));
        doThrow(new RuntimeException()).when(categoryMockLogic).editCategoryHierarchy(
                cat,parents,childs);
        String expected = "Beim Updaten der Hierarchie ist ein Fehler aufgetreten.";
        final String[] actual = new String[1];
        categoryController.editCategoryHierarchy(cat,parents,childs, new SingleDataCallback<>() {
            @Override
            public void onSuccess(String data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }
    @Test
    public void editCategoryHierarchyTest(){
        final Category cat = new Category("Test");
        final List<Category> parents = Arrays.asList(new Category("Parent1"),new Category("Parent2"));
        final List<Category> childs = Arrays.asList(new Category("Childs1"),new Category("Childs2"));

        categoryController.editCategoryHierarchy(cat,parents,childs, new SingleDataCallback<>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFailure(String msg) {
            }

        });

    }

      



}
