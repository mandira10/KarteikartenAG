package com.swp.Logic.CardLogicTest;

import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.CardToTag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.joor.Reflect.on;
import static org.mockito.Mockito.mock;

public class CardLogicTest {
    private CardLogic cardLogic;
    private CardRepository cardRepository;
    private CardToCategory cardToCategory;
    private CardToTag cardToTag;

    @BeforeEach
    public void beforeEach() {
        cardRepository = mock(CardRepository.class);
        //cardToCategory = mock(CardToCategory.class);
        //cardToDeck = mock(CardToDeck.class);
        //cardToTag = mock(CardToTag.class);
        cardLogic = new CardLogic();

        on(cardLogic).set("baseRepository", cardRepository);
    }

    @Test
    public void testExceptionIfNullQuestion() {

    }

    @Test
    public void testExceptionDatabase() {

    }
}
