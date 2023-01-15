package com.swp.Logic;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;


import static com.swp.TestData.importTestData;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Slf4j
/**
 * Testclass for updateFunction of Categories to CardToCategory and CategoryHierarchy
 */
public class TestC2CCH {



    @Test
    public void testCreateOneCategoryForCardNoExistingCat(){
        Category catToAdd = new Category("TestKategorie1");
        ArrayList<Category> categoryToAdd = new ArrayList<>() {
            {
                add(catToAdd);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2",false);
        //add card to database
        assertTrue(CardLogic.updateCardData(card1,true));
        //add cardTocategory for own category
        assertTrue(CategoryLogic.setC2COrCH(card1,categoryToAdd,false));
        //check if C2C was added successfully
        List<Category> cat = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(cat.isEmpty());
        assertEquals(cat.size(),1);
    }
    @Test
    public void testCardCreateAndUpdateWithCategoriesNoExistingCat(){
        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category("TestKategorie1"));
                add(new Category("TestKategorie2"));
            }
        };
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel2",false);
        //add card to database
        assertTrue(CardLogic.updateCardData(card2,true));
        //add cardTocategory for own category
        assertTrue(CategoryLogic.setC2COrCH(card2,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> cat = CategoryLogic.getCategoriesByCard(card2);
        assertFalse(cat.isEmpty());
        assertEquals(cat.size(),2);
    }

    @Test
    public void testCardCreateAndUpdateWithCategoriesWithExistingCat(){
        importTestData();
        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category("Erdkunde"));
                add(new Category("Spanisch"));
            }
        };
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel2",false);
        //add card to database
        assertTrue(CardLogic.updateCardData(card2,true));
        //add cardTocategory for own category
        assertTrue(CategoryLogic.setC2COrCH(card2,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> cat = CategoryLogic.getCategoriesByCard(card2);
        assertFalse(cat.isEmpty());
        assertEquals(cat.size(),2);
    }

    @Test
    public void addSameCategoriesToExistingCard(){
        importTestData();
        List<Card> cardsWithCategories = CategoryLogic.getCardsInCategory("Erdkunde");
        assertFalse(cardsWithCategories.isEmpty());
        Card card1 = cardsWithCategories.get(0);
        List<Category> catOld = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catOld.isEmpty());
        assertEquals(2,catOld.size());
        //name of existing categories
        String name1 = catOld.get(0).getName();
        String name2 = catOld.get(1).getName();

        List<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category(name1));
                add(new Category(name2));
            }
        };
        //add cardTocategory for new categories
        assertTrue(CategoryLogic.setC2COrCH(card1,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> catNew = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catNew.isEmpty());
        assertEquals(2,catNew.size());
    }



    @Test
    public void addNewCategoriesToExistingCard(){
        importTestData();
        List<Card> cardsWithCategories = CategoryLogic.getCardsInCategory("Spanisch");
        assertFalse(cardsWithCategories.isEmpty());
        Card card1 = cardsWithCategories.get(0);
        List<Category> catOld = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catOld.isEmpty());
        //name of existing categories
        String name1 = catOld.get(0).getName();
        String name2 = catOld.get(1).getName();

        List<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category(name1));
                add(new Category(name2));
                add(new Category("Test"));
                add(new Category("Erdkunde"));
            }
        };
        //add cardTocategory for new categories
        assertTrue(CategoryLogic.setC2COrCH(card1,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> catNew = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catNew.isEmpty());
        assertEquals(4,catNew.size());
    }

    @Test
    public void RemoveCategoriesFromExistingCard(){
        importTestData();
        List<Card> cardsWithCategories = CategoryLogic.getCardsInCategory("Technik");
        assertFalse(cardsWithCategories.isEmpty());
        Card card1 = cardsWithCategories.get(0);
        List<Category> catOld = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catOld.isEmpty());
        assertEquals(2,catOld.size());
        //name of existing categories
        String name1 = catOld.get(0).getName();
        String name2 = catOld.get(1).getName();

        List<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category(name1));
            }
        };
        //add cardTocategory for new categories
        assertTrue(CategoryLogic.setC2COrCH(card1,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> catNew = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catNew.isEmpty());
        assertEquals(1,catNew.size());
    }


    @Test
    public void RemoveAndAddCategoriesToExistingCard(){
        importTestData();
        List<Card> cardsWithCategories = CategoryLogic.getCardsInCategory("random");
        assertFalse(cardsWithCategories.isEmpty());
        Card card1 = cardsWithCategories.get(0);
        List<Category> catOld = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catOld.isEmpty());
        assertEquals(1,catOld.size());
        //name of existing categories
        String name1 = catOld.get(0).getName();

        List<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category("Test"));
                add(new Category("Spanisch"));
            }
        };
        //add cardTocategory for new categories
        assertTrue(CategoryLogic.setC2COrCH(card1,categoriesToAdd,false));
        //check if C2C was added successfully
        List<Category> catNew = CategoryLogic.getCategoriesByCard(card1);
        assertFalse(catNew.isEmpty());
        assertEquals(2,catNew.size());
    }


    @Test
    public void testCategoryMultiHierarchy() {
        Category gymnasium = new Category("Gymnasium");
        Category oberschule = new Category("Oberschule");
        Category technik = new Category("Technik");
        Category biologie = new Category("Biologie");
        Category klasse10 = new Category("Klasse10");
        Category klasse11 = new Category("Klasse11");
        Category fotosynthese = new Category("Fotosynthese");
        Category genetik = new Category("Genetik");
        List<Category> categories = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
                add(technik);
                add(biologie);
                add(klasse11);
                add(klasse10);
                add(fotosynthese);
                add(genetik);
            }
        };
        for(Category c : categories){
            assertTrue(CategoryLogic.updateCategoryData(c,true));
        }
        List<Category> childTechnik = new ArrayList<>() {
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
            }
        };
        List<Category> parentsTechnik = new ArrayList<>() {
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
        assertTrue(CategoryLogic.setC2COrCH(technik,parentsTechnik,true));
        assertTrue(CategoryLogic.setC2COrCH(technik,childTechnik,false));
        assertTrue(CategoryLogic.setC2COrCH(biologie,parentsBiologie,true));
        assertTrue(CategoryLogic.setC2COrCH(biologie,childsBiologie,false));
        assertTrue(CategoryLogic.setC2COrCH(klasse11,childsklasse11,false));



    }
}
