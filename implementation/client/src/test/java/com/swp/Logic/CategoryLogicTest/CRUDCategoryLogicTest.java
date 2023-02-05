package com.swp.Logic.CategoryLogicTest;

import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.Language.German;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Klasse, die die normalen Funktionen für eine Kategorie testet, mithilfe von Komponententests.
 *
 *  @author Nadja Cordes
 */
public class CRUDCategoryLogicTest {

    private CategoryRepository categoryRepMock;
    private final CategoryLogic categoryLogic = CategoryLogic.getInstance();

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
     * Before-Each Tests Methode.
     * Die Repos werden gemockt und in der Logik als gemockt gesetzt.
     */
    @BeforeEach
    public void beforeEach(){
        categoryRepMock = mock(CategoryRepository.class);
        on(categoryLogic).set("categoryRepository",categoryRepMock);
    }

    /**
     * Testet die delete Funktion, wenn null übergangen wird
     */
    @Test
    public void testExceptionIfCategoryToDeleteIsNull() {
        final String expected = "Kategorie existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> categoryLogic.deleteCategory(null));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die delete Funktion mit einer Kategorie
     */
    @Test
    public void testDeleteCategoryFunction(){
        Category category  = new Category("Test");
        doNothing().when(categoryRepMock).delete(category);
        categoryLogic.deleteCategory(category);
        verify(categoryRepMock, times(1)).delete(any(Category.class));

    }

    /**
     * Testet die delete Funktion mit mehreren Kategorien
     */
    @Test
    public void testDeleteFunctionForManyCategories(){
       Category cat1 = new Category("Test");
       Category cat2 = new Category("Test2");
        List<Category> cats = Arrays.asList(cat2,cat1);
        doNothing().when(categoryRepMock).delete(any(Category.class));
        categoryLogic.deleteCategories(cats);
        verify(categoryRepMock, times(2)).delete(any(Category.class));

    }

    /**
     * Testet die update Funktion, wenn null übergangen wird
     */
    @Test
    public void testExceptionIfCategoryToUpdateIsNull() {
        final String expected = "categorynullerror";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> categoryLogic.updateCategoryData(null,true,false));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die update Funktion, wenn eine neue Kategorie gesaved werden soll, deren Name bereits in der Datenbank vorhanden ist.
     * Wirft eine Exception, die an den Controller weitergegeben wird.
     */
    @Test
    public void testExceptionIfCategoryNameAlreadyExistsSave() {
        final String expected = "categorywithnameexistsalready";
        Category catToAdd = new Category("Test");
        Category existingCat = new Category("Test");
        when(categoryRepMock.find("Test")).thenReturn(existingCat);
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.updateCategoryData(catToAdd,true,false));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die update Funktion, wenn eine bestehende Kategorie geupdatet werden soll, deren Name geändert werden soll.
     * Wirft eine Exception, die an den Controller weitergegeben wird.
     */
    @Test
    public void testExceptionIfCategoryNameAlreadyExistsUpdate() {
        final String expected = "categorywithnameexistsalready";
        Category catToChange = new Category("Test");
        Category existingCat = new Category("Test");
        when(categoryRepMock.find("Test")).thenReturn(existingCat);
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.updateCategoryData(catToChange,false,true));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die update Funktion, wenn eine bestehende Kategorie geupdatet werden soll, deren Name geändert werden soll.
     * Catcht die NoResultException der Logik und ruft save auf.
     */
    @Test
    public void testIfCategoryNameDoesNotAlreadyExistsUpdate() {
        Category catToChange = new Category("Test");
        when(categoryRepMock.save(catToChange)).thenReturn(catToChange);
        when(categoryRepMock.find("Test")).thenThrow(new NoResultException("Test"));
                assertDoesNotThrow(() -> categoryLogic.updateCategoryData(catToChange,false,true));
        verify(categoryRepMock).save(argThat(new ArgumentMatcher<Category>() {
            @Override
            public boolean matches(Category cat) {
                cat.equals(catToChange);
                return true;
            }
        }));
    }

    /**
     * Testet die Save Methode beim UpdateSave von Category.
     */
    @Test
    public void testSaveFunction() {
        Category catToAdd = new Category("Test");
        when(categoryRepMock.find("Test")).thenThrow(NoResultException.class);
        when(categoryRepMock.save(catToAdd)).thenReturn(catToAdd);
        categoryLogic.updateCategoryData(catToAdd,true,false);
        verify(categoryRepMock).save(argThat(new ArgumentMatcher<Category>() {
            @Override
            public boolean matches(Category cat) {
                cat.equals(catToAdd);
                return true;
            }
        }));
    }

    /**
     * Testet die update Methode, wenn eine Category geupdatet wird ohne Namensänderung
     */
    @Test
    public void testUpdateFunction() {
        Category catToAdd = new Category("Test");
        when(categoryRepMock.update(catToAdd)).thenReturn(catToAdd);
        categoryLogic.updateCategoryData(catToAdd,false,false);
        verify(categoryRepMock).update(argThat(new ArgumentMatcher<Category>() {
            @Override
            public boolean matches(Category cat) {
                cat.equals(catToAdd);
                return true;
            }
        }));
    }

    /**
     * Testet die getCategoryByUUID methode,wenn null übergeben wird.
     */
    @Test
    public void testGetCategoryByUUIDNullException(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCategoryByUUID(null));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die getCategoryByUUID methode,wenn empty übergeben wird.
     */
    @Test
    public void testGetCategoryByUUIDEmptyException(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCategoryByUUID(""));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die getCategoryByUUID methode
     */
    @Test
    public void testGetCategoryByUUID(){
        Category category = new Category("Test");
        when(categoryRepMock.findByUUID(any(String.class))).thenReturn(category);
        Category category1 = categoryLogic.getCategoryByUUID("Test");
        assertEquals(category,category1);
    }

    /**
     * Testet getCategories
     */
    @Test
    public void testGetCategories(){
        List list = new ArrayList();
        when(categoryRepMock.getAll()).thenReturn(list);
        List list1 = categoryLogic.getCategories();
        assertEquals(list,list1);
    }
}
