package com.amazonaws.pojo;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

import java.io.Serializable;

@Data
@DynamoDBTable(tableName = "MATCH")
public class Match implements Serializable {

    private static final long serialVersionUID = -8243145429438016232L;
    public static final String AWAY_TEAM_INDEX = "AwayTeam";

    @DynamoDBAttribute
    private Long matchId;

    @DynamoDBRangeKey
    private Long matchDate;

    @DynamoDBHashKey
    private String homeTeam;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = AWAY_TEAM_INDEX)
    private String awayTeam;



    public Match() { }

    public Match(String team, Long date) {
        this.homeTeam = team;
        this.matchDate = date;
    }

    public Match(Long eventId, Long eventDate, String homeTeam, String awayTeam) {
        this.matchId = eventId;
        this.matchDate = eventDate;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The match of ");
        sb.append(homeTeam);
        sb.append(" will be hold on ");
        sb.append(matchDate);
        return sb.toString();
    }
}
