package com.swp.Logic.CategoryLogicTest;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Category;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToCategoryRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.TagRepository;
import jakarta.persistence.NoResultException;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testclass for updateFunction of Categories to CardToCategory and CategoryHierarchy
 */
public class TestUpdateCardToCategoryAndCategoryHierarchy {

    CardLogic cardLogic = CardLogic.getInstance();
    CategoryLogic categoryLogic = CategoryLogic.getInstance();
    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CardToCategoryRepository cardToCategoryRepMock;

    @Before
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        cardToCategoryRepMock = mock(CardToCategoryRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
        on(categoryLogic).set("categoryRepository",categoryRepMock);
        on(categoryLogic).set("cardRepository",cardRepMock);
        on(categoryLogic).set("cardToCategoryRepository",cardToCategoryRepMock);
    }


    @Test
    public void testAddOneCardToCategoryNotExisting(){
        Category catToAdd = new Category("TestKategorie1");
        ArrayList<Category> categoryToAdd = new ArrayList<>() {
            {
                add(catToAdd);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2",false);
        List<CardOverview> cards = Arrays.asList(new CardOverview());
        ArrayList<Category> categoriesToReturn = new ArrayList<>();
        //mock
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categoriesToReturn);
        when(categoryRepMock.find(catToAdd.getName())).thenThrow(NoResultException.class);
        doNothing().when(categoryRepMock).save(catToAdd);
        doNothing().when(cardToCategoryRepMock).save(new CardToCategory(card1, catToAdd));
        //add cardTocategory for own category
        categoryLogic.setC2COrCH(card1,categoryToAdd,false);
    }

    @Test
    public void testMoreThanOneNotExistingCategoryToAdd(){
        Category catToAdd = new Category("TestKategorie1");
        Category catToAdd1 = new Category("TestKategorie2");
        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
               add(catToAdd);
               add(catToAdd1);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2",false);
        List<CardOverview> cards = Arrays.asList(new CardOverview());
        ArrayList<Category> categoriesToReturn = new ArrayList<>();
        //mock
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categoriesToReturn);
        when(categoryRepMock.find(catToAdd1.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.find(catToAdd.getName())).thenThrow(NoResultException.class);
        doNothing().when(categoryRepMock).save(catToAdd);
        doNothing().when(categoryRepMock).save(catToAdd1);
        doNothing().when(cardToCategoryRepMock).save(new CardToCategory(card1, catToAdd));
        doNothing().when(cardToCategoryRepMock).save(new CardToCategory(card1, catToAdd1));
        //add cardTocategory for own category
        categoryLogic.setC2COrCH(card1,categoriesToAdd,false);
    }


    @Test
    public void testExistingOnlyCategoriesToAdd() {
        Category exCat1 = new Category("Erdkunde");
        Category exCat2 = new Category("Spanisch");
        ArrayList<Category> categories = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
            }
        };

        Category newCat1 = new Category("Erdkunde");
        Category newCat2 = new Category("Spanisch");
        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(newCat1);
                add(newCat2);
            }
        };


        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2", false);

        List<CardOverview> cards = Arrays.asList(new CardOverview());
        //mock that exact categories to add are returned
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categories);
        categoryLogic.setC2COrCH(card1, categoriesToAdd, false);
    }

    @Test
    public void addNewCategoriesToExistingCategories() {
        Category exCat1 = new Category("Erdkunde");
        Category exCat2 = new Category("Spanisch");
        ArrayList<Category> existingCategories = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
            }
        };

        Category newCat1 = new Category("Deutsch");
        Category newCat2 = new Category("Englisch");

        ArrayList<Category> categorieToAdd = new ArrayList<>() {
            {
                add(newCat1);
                add(newCat2);
                add(exCat1);
                add(exCat2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2", false);
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
        when(categoryRepMock.find(newCat1.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.find(newCat2.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.find(exCat1.getName())).thenReturn(exCat1);
        when(categoryRepMock.find(exCat2.getName())).thenReturn(exCat2);
        doNothing().when(categoryRepMock).save(newCat1);
        doNothing().when(categoryRepMock).save(newCat2);
        doNothing().when(cardToCategoryRepMock).save(new CardToCategory(card1, newCat1));
        doNothing().when(cardToCategoryRepMock).save(new CardToCategory(card1, newCat2));

        categoryLogic.setC2COrCH(card1, categorieToAdd, false);
    }

    @Test
    public void RemoveCategoriesFromExistingCard() {
        Category exCat1 = new Category("Erdkunde");
        Category exCat2 = new Category("Spanisch");
        Category exCat3 = new Category("Deutsch");
        Category exCat4 = new Category("Englisch");
        ArrayList<Category> existingCategories = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
                add(exCat3);
                add(exCat4);
            }
        };

        Category newCat1 = new Category("Deutsch");
        Category newCat2 = new Category("Englisch");

        ArrayList<Category> categorieToAdd = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2", false);
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
        CardToCategory cardToCategory = new CardToCategory(card1,exCat3);
        when(cardToCategoryRepMock.getSpecific(any(Card.class),any(Category.class))).thenReturn(cardToCategory);
        doNothing().when(cardToCategoryRepMock).delete((any(CardToCategory.class)));
        categoryLogic.setC2COrCH(card1, categorieToAdd, false);

    }


    @Test
    public void RemoveAndAddCategoriesToExistingCard() {
        Category exCat1 = new Category("Erdkunde");
        Category exCat2 = new Category("Spanisch");
        Category exCat3 = new Category("Deutsch");
        Category exCat4 = new Category("Englisch");
        ArrayList<Category> existingCategories = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
                add(exCat3);
                add(exCat4);
            }
        };

        Category newCat1 = new Category("Deutsch");
        Category newCat2 = new Category("Englisch");
        //Category exCat3 = new Category("Deutsch");
        //Category exCat4 = new Category("Englisch");

//        ArrayList<Category> categorieToAdd = new ArrayList<>() {
//            {
//                add(exCat1);
//                add(exCat2);
//            }
//        };
//        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2", false);
//        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
//        CardToCategory cardToCategory = new CardToCategory(card1,exCat3);
//        when(cardToCategoryRepMock.getSpecific(any(Card.class),any(Category.class))).thenReturn(cardToCategory);
//        doNothing().when(cardToCategoryRepMock).delete((any(CardToCategory.class)));
//        categoryLogic.setC2COrCH(card1, categorieToAdd, false);

    }
//
//
//
//    @Test
//    public void testCategoryMultiHierarchy() {
//        Category gymnasium = new Category("Gymnasium");
//        Category oberschule = new Category("Oberschule");
//        Category technik = new Category("Technik");
//        Category biologie = new Category("Biologie");
//        Category klasse10 = new Category("Klasse10");
//        Category klasse11 = new Category("Klasse11");
//        Category fotosynthese = new Category("Fotosynthese");
//        Category genetik = new Category("Genetik");
//        List<Category> categories = new ArrayList<>() {
//            {
//                add(gymnasium);
//                add(oberschule);
//                add(technik);
//                add(biologie);
//                add(klasse11);
//                add(klasse10);
//                add(fotosynthese);
//                add(genetik);
//            }
//        };
//        for (Category c : categories) {
//            categoryLogic.updateCategoryData(c, true);
//        }
//        List<Category> childTechnik = new ArrayList<>() {
//            {
//                add(klasse11);
//                add(klasse10);
//            }
//        };
//        List<Category> childsBiologie = new ArrayList<>() {
//            {
//                add(klasse11);
//                add(klasse10);
//            }
//        };
//        List<Category> childsklasse11 = new ArrayList<>() {
//            {
//                add(fotosynthese);
//                add(genetik);
//            }
//        };
//        List<Category> parentsTechnik = new ArrayList<>() {
//            {
//                add(gymnasium);
//                add(oberschule);
//            }
//        };
//        List<Category> parentsBiologie = new ArrayList<>() {
//            {
//                add(gymnasium);
//                add(oberschule);
//            }
//        };
//        categoryLogic.setC2COrCH(technik, parentsTechnik, true);
//        categoryLogic.setC2COrCH(technik, childTechnik, false);
//        categoryLogic.setC2COrCH(biologie, parentsBiologie, true);
//        categoryLogic.setC2COrCH(biologie, childsBiologie, false);
//        categoryLogic.setC2COrCH(klasse11, childsklasse11, false);
//
//
//    }
}
