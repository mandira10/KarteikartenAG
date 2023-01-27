package com.swp.Logic.CategoryLogicTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Category;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToCategoryRepository;
import com.swp.Persistence.CategoryRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.joor.Reflect.on;
import static org.mockito.Mockito.*;

/**
 * Testclass for updateFunction of Categories to CardToCategory and CategoryHierarchy
 */
public class UpdateCardToCategoryAndCategoryHierarchyTest {

    CategoryLogic categoryLogic = CategoryLogic.getInstance();
    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CardToCategoryRepository cardToCategoryRepMock;
    private Locale locale = new Locale("German", "de");
    private int i;

    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        cardToCategoryRepMock = mock(CardToCategoryRepository.class);
        on(categoryLogic).set("categoryRepository",categoryRepMock);
        on(categoryLogic).set("cardRepository",cardRepMock);
        on(categoryLogic).set("cardToCategoryRepository",cardToCategoryRepMock);

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
     * Testet das Hinzufügen einer einzelnen Kategorie zu einer Karte, wenn diese noch keine Kategorien hat.
     */
    @Test
    public void testAddOneCardToCategoryNotExisting(){
        //Testdaten
        Category catToAdd = new Category("TestKategorie1");
        ArrayList<Category> categoryToAdd = new ArrayList<>() {
            {
                add(catToAdd);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2");
        ArrayList<Category> categoriesToReturn = new ArrayList<>();

        //Karte hat noch keine Kategorien
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categoriesToReturn);
        //Die angegebene Kategorie ist noch nicht in der Datenbank gespeichert, mocke das Speichern der Kategorie
        when(categoryRepMock.find(catToAdd.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(catToAdd)).thenReturn(catToAdd);
        //Kein tatsächliches Speichern
        CardToCategory c1 = new CardToCategory(card1,catToAdd);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, catToAdd))).thenReturn(c1);

        //Test
        categoryLogic.setCardToCategories(card1,categoryToAdd);
    }

    /**
     * Testet das Hinzufügen mehrerer  Kategorien zu einer Karte, wenn diese ncoh keine Kategorien hat.
     */
    @Test
    public void testMoreThanOneNotExistingCategoryToAdd(){
        //Testdaten
        Category catToAdd = new Category("TestKategorie1");
        Category catToAdd1 = new Category("TestKategorie2");
        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
               add(catToAdd);
               add(catToAdd1);
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2");
        ArrayList<Category> categoriesToReturn = new ArrayList<>();

        //Karte hat noch keine Kategorien
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categoriesToReturn);
        //CatToAdd1 ist noch nciht in Datenbank gespeichert, mocke das Speichern
        when(categoryRepMock.find(catToAdd1.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(catToAdd1)).thenReturn(catToAdd1);
        //CatToAdd ist bereits gespeichert und kann verwendet werden
        when(categoryRepMock.find(catToAdd.getName())).thenReturn(catToAdd);
        //Kein tatsächliches Speichern
        CardToCategory c1 = new CardToCategory(card1,catToAdd);
        CardToCategory c2 = new CardToCategory(card1,catToAdd1);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, catToAdd))).thenReturn(c1);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, catToAdd1))).thenReturn(c2);

        //Test
        categoryLogic.setCardToCategories(card1,categoriesToAdd);
    }

    /**
     * Testet, ob nichts getan wird, wenn die übergebenen Kategorien die gleichen sind, wie die bereits zugehörigen.
     */
    @Test
    public void testExistingOnlyCategoriesToAdd() {
        //Testdaten
        Category exCat1 = new Category("Erdkunde");
        Category exCat2 = new Category("Spanisch");
        ArrayList<Category> categories = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
            }
        };

        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");

        //Die zugehörigen Kategorien sind die gleichen wie die hinzugefügten
        //kein Handling nötig
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(categories);

        //Test
        categoryLogic.setCardToCategories(card1, categories);
    }

    /**
     * Testet das Hinzufügen neuer Kategorien zu einer Karte, wenn diese bereits zugehörige Kategorien hat.
     */
    @Test
    public void addNewCategoriesToExistingCategories() {
        //Testdaten
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
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");
        //Bereits gespeicherte Kategorien zu der Karte werden returned
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
        //Die neuen Kategorien sind noch nicht in DB gespeichert, die alten werden gar nicht gesucht
        when(categoryRepMock.find(newCat1.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.find(newCat2.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(newCat1)).thenReturn(newCat1);
        when(categoryRepMock.save(newCat2)).thenReturn(newCat2);
        //Tue nichts beim Hinzufügen der neuen CardToCategories
        CardToCategory c1 = new CardToCategory(card1,newCat1);
        CardToCategory c2 = new CardToCategory(card1,newCat2);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, newCat1))).thenReturn(c1);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, newCat2))).thenReturn(c2);

        //Test
        categoryLogic.setCardToCategories(card1, categorieToAdd);
    }

    /**
     * Testet das manuelle Löschen von Kategorien zu einer Karte mit bereits bestehenden Kategorien.
     */
    @Test
    //testdaten
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

        ArrayList<Category> categorieToAdd = new ArrayList<>() {
            {
                add(exCat1);
                add(exCat2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");
        //Bereits gespeicherte Kategorien zu der Karte
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
        //Manuell angelegte cardToCategories für die zu löschenden Kategorien
        CardToCategory cardToCategory = new CardToCategory(card1,exCat3);
        CardToCategory cardToCategory1 = new CardToCategory(card1,exCat4);
        //Mocke das Repo, so dass diese beim Suchen zurückgegeben werden
        when(cardToCategoryRepMock.getSpecific(card1,exCat3)).thenReturn(cardToCategory);
        when(cardToCategoryRepMock.getSpecific(card1,exCat4)).thenReturn(cardToCategory1);
        //Tue nichts wenn die Card To Categories gelöscht werden sollen
        doNothing().when(cardToCategoryRepMock).delete((any(CardToCategory.class)));

        //test
        categoryLogic.setCardToCategories(card1, categorieToAdd);

    }

    /**
     * Teste das Löschen und Hinzufügen von Kategorien zu einer Karte
     */
    @Test
    public void RemoveAndAddCategoriesToExistingCard() {
        //testdaten
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

        ArrayList<Category> categoriesToAdd = new ArrayList<>() {
            {
               add(exCat1);
               add(newCat1);
               add(newCat2);
            }
        };
        Card card1 = new TextCard("Testfrage1", "Testantwort1", "Testtitel2");

        //Bereits gespeicherte Kategorien zu der Karte
        when(categoryRepMock.getCategoriesToCard(card1)).thenReturn(existingCategories);
        //Zu löschendes manuell angelegtes Card To Category, gib dieses bei Suche zurück
        CardToCategory cardToCategory = new CardToCategory(card1,exCat2);
        when(cardToCategoryRepMock.getSpecific(card1,exCat2)).thenReturn(cardToCategory);
        //Tue nichts beim Löschen
        doNothing().when(cardToCategoryRepMock).delete((any(CardToCategory.class)));
        //Neue Kategorien noch nicht in DB gespeichert, alte wird nicht gesucht
        when(categoryRepMock.find(newCat1.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.find(newCat2.getName())).thenThrow(NoResultException.class);
        //Tue nichts beim Speichern der neuen Kategorien und CardToCategories
        when(categoryRepMock.save(newCat1)).thenReturn(newCat1);
        when(categoryRepMock.save(newCat2)).thenReturn(newCat2);
        CardToCategory c1 = new CardToCategory(card1,newCat1);
        CardToCategory c2 = new CardToCategory(card1,newCat2);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, newCat1))).thenReturn(c1);
        when(cardToCategoryRepMock.save(new CardToCategory(card1, newCat2))).thenReturn(c2);

        //Test
        categoryLogic.setCardToCategories(card1, categoriesToAdd);
    }

    /**
     * Testet das Hinzufügen von Children und Parents für eine Kategorie, die noch keine Parents oder Children hat
     */
    @Test
    public void testCategoryMultiHierarchyAddNewParentsAndChildren() {
        //Testdaten
        Category gymnasium = new Category("Gymnasium");
        Category oberschule = new Category("Oberschule");
        Category technik = new Category("Technik");
        Category klasse10 = new Category("Klasse10");
        Category klasse11 = new Category("Klasse11");
        List<Category> childTechnik = new ArrayList<>() {
            {
                add(klasse11);
                add(klasse10);
            }
        };
        List<Category> parentsTechnik = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
            }
        };
        //Gib leere Listen für Eltern und Parents zurück
        when(categoryRepMock.getChildrenForCategory(technik)).thenReturn(new ArrayList<>());
        when(categoryRepMock.getParentsForCategory(technik)).thenReturn(new ArrayList<>());
        //Manche Kategorien sind noch nicht in DB, tue nichts beim Speicheraufruf
        when(categoryRepMock.find(klasse10.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(klasse10)).thenReturn(klasse10);
        when(categoryRepMock.find(gymnasium.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(gymnasium)).thenReturn(gymnasium);
        //Finde die anderen Kategorien in DB
        when(categoryRepMock.find(klasse11.getName())).thenReturn(klasse11);
        when(categoryRepMock.find(oberschule.getName())).thenReturn(oberschule);

        //Speicher keine der neuen Hierarchien
        doNothing().when(categoryRepMock).saveCategoryHierarchy(klasse10, technik);
        doNothing().when(categoryRepMock).saveCategoryHierarchy(klasse11, technik);
        doNothing().when(categoryRepMock).saveCategoryHierarchy(technik, gymnasium);
        doNothing().when(categoryRepMock).saveCategoryHierarchy(technik, oberschule);

        //Test
        categoryLogic.setCategoryHierarchy(technik, parentsTechnik, true);
        categoryLogic.setCategoryHierarchy(technik, childTechnik, false);
    }
    /**
     * Testet das komplette Löschen von Children und Parents für eine Kategorie, die bereits Children und Parents hat.
     */
    @Test
    public void testCategoryMultiHierarchyRemoveParentsAndChildren() {
        //Testdaten
        Category gymnasium = new Category("Gymnasium");
        Category oberschule = new Category("Oberschule");
        Category technik = new Category("Technik");
        Category klasse10 = new Category("Klasse10");
        Category klasse11 = new Category("Klasse11");
        List<Category> childTechnik = new ArrayList<>() {
            {
                add(klasse11);
                add(klasse10);
            }
        };
        List<Category> parentsTechnik = new ArrayList<>() {
            {
                add(gymnasium);
                add(oberschule);
            }
        };
        //Gib die Kategorien als Children und Parents zurück
        when(categoryRepMock.getChildrenForCategory(technik)).thenReturn(childTechnik);
        when(categoryRepMock.getParentsForCategory(technik)).thenReturn(parentsTechnik);
        //Mache nichts beim Löschen der Kategorien
        doNothing().when(categoryRepMock).deleteCategoryHierarchy(any(Category.class),any(Category.class));

        //Test
        categoryLogic.setCategoryHierarchy(technik, new ArrayList<>(), true);
        categoryLogic.setCategoryHierarchy(technik, new ArrayList<>(), false);

    }

    /**
     * Testet das Löschen und Hinzufügen von Children und Parents für eine Kategorie, die bereits Children und Parents hat.
     */
    @Test
    public void testCategoryMultiHierarchyRemoveAndCreateParentsAndChildren() {
        //Testdaten
        Category gymnasium = new Category("Gymnasium");
        Category oberschule = new Category("Oberschule");
        Category technik = new Category("Technik");
        Category klasse10 = new Category("Klasse10");
        Category klasse11 = new Category("Klasse11");
        List<Category> oldChildTechnik = new ArrayList<>() {
            {
                add(klasse11);
            }
        };
        List<Category> newChildTechnik = new ArrayList<>() {
            {
                add(klasse10);
            }
        };
        List<Category>oldParentsTechnik = new ArrayList<>() {
            {
                add(oberschule);
            }
        };
        List<Category>newParentsTechnik = new ArrayList<>() {
            {
                add(gymnasium);

            }
        };
        //Gib alte Parents und Children für Kategorie zurück
        when(categoryRepMock.getChildrenForCategory(technik)).thenReturn(oldChildTechnik);
        when(categoryRepMock.getParentsForCategory(technik)).thenReturn(oldParentsTechnik);
        //Tue nichts, wenn die alten Childs/Parents gelöscht werden
        doNothing().when(categoryRepMock).deleteCategoryHierarchy(any(Category.class),any(Category.class));
        //Neues Child noch nicht in Db enthalten, tue nichts beim Save
        when(categoryRepMock.find(klasse10.getName())).thenThrow(NoResultException.class);
        when(categoryRepMock.save(klasse10)).thenReturn(klasse10);
        //Parent bereits in DB enthalten, finde diesen
        when(categoryRepMock.find(gymnasium.getName())).thenReturn(gymnasium);

        //Kein richtiges Speichern
        doNothing().when(categoryRepMock).saveCategoryHierarchy(klasse10, technik);
        doNothing().when(categoryRepMock).saveCategoryHierarchy(technik, gymnasium);

        //Test
        categoryLogic.setCategoryHierarchy(technik,newParentsTechnik, true);
        categoryLogic.setCategoryHierarchy(technik, newChildTechnik, false);

    }
}
