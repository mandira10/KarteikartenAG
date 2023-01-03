package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.query.named.NamedObjectRepository;

import java.sql.Timestamp;

@Entity
@Immutable
@Subselect(value = "SELECT c.UUID as uUUID," +
       "c.CREATIONDATE as cardCreated," +
       "CASE WHEN c.TITLE IS null THEN c.QUESTION ELSE c.TITLE END AS titelToShow," +
       "COUNT(SELECT cd.ID FROM CARDTODECK cd WHERE cd.CARD_UUID = c.UUID) AS countDecks " +
       "FROM CARD c " +
       "GROUP BY c.UUID")
@Synchronize({"CARD", "CARDTODECK"})
@Getter
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

    public CardOverview(){}


}
