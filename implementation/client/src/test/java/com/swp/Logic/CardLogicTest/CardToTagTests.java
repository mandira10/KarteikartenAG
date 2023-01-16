package com.swp.Logic.CardLogicTest;

import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToTagRepository;
import com.swp.Persistence.TagRepository;
import org.junit.Before;

import static org.joor.Reflect.on;
import static org.mockito.Mockito.mock;

/**
 * Diese Testklasse untersucht alle CardLogic Funktionen für die Card2Tags.
 */
public class CardToTagTests {
    private CardRepository cardRepMock;
    private TagRepository tagRepMock;

    private CardToTagRepository c2cRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();


    @Before
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        tagRepMock = mock(TagRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
    }

    public void testCreateTags(){}
    public void testGetCard2Tags(){}
    public void getTags(){}
}
