package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.query.named.NamedObjectRepository;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Zeigt die einzelnen Karten mitsamt ihrem Erstellzeitpunkt und in wievielen Desks sie derzeit sind.
 */
@Entity
@Immutable
@Getter
@Subselect(value = "SELECT c.UUID as uUUID," +
       "c.CREATIONDATE as cardCreated," +
        "c.content as content,"+
       "CASE WHEN c.TITLE = '' THEN c.QUESTION ELSE c.TITLE END AS titelToShow," +
       "COUNT(SELECT DISTINCT b2C.ID FROM BOXTOCARD b2C WHERE b2C.CARD_UUID = c.UUID) AS countDecks " +
       "FROM CARD c " +
       "GROUP BY c.UUID " +
       "ORDER BY c.TITLE")
@Synchronize({"CARD", "BOXTOCARD"})
@NamedQuery(name  = "CardOverview.findCardsByContent",
        query = "SELECT c FROM CardOverview c WHERE LOWER(c.content) LIKE LOWER(:content)")
@NamedQuery(name = "CardOverview.allCardsWithTag",
        query = "SELECT c FROM CardOverview c LEFT JOIN CardToTag c2t ON c2t.card = c.uUUID WHERE c2t.tag = :tag")
@NamedQuery(name = "CardOverview.allCardsWithStudySystem",
        query = "SELECT c FROM CardOverview c LEFT JOIN BoxToCard b2c ON b2c.card = c.uUUID LEFT JOIN StudySystemBox sbox ON sbox.id = b2c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem")

public class CardOverview  {


    @Id
    @Column
    private String uUUID;

    @Column
    private int countDecks;

    @Column
    private Timestamp cardCreated;

    @Column
    private String titelToShow;
    @Column
    private String content;


    public CardOverview(){}


}
