package com.amazonaws.dao;

import com.amazonaws.manager.DynamoDBManager;
import com.amazonaws.pojo.Match;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.apache.log4j.Logger;

import java.util.*;

public class MatchDaoImpl implements MatchDao {

    private static final Logger log = Logger.getLogger(MatchDaoImpl.class);

    private static final DynamoDBMapper mapper = DynamoDBManager.mapper();

    private static volatile MatchDaoImpl instance;

    @Override
    public List<Match> findAllMatches() {
        return mapper.scan(Match.class, new DynamoDBScanExpression());
    }


    @Override
    public List<Match> findMatchByTeam(String team) {
        DynamoDBQueryExpression<Match> homeQuery = new DynamoDBQueryExpression<>();
        Match eventKey = new Match();
        eventKey.setHomeTeam(team);
        homeQuery.setHashKeyValues(eventKey);
        List<Match> homeEvents = mapper.query(Match.class, homeQuery);

        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":v1", new AttributeValue().withS(team));
        DynamoDBQueryExpression<Match> awayQuery = new DynamoDBQueryExpression<Match>()
                .withIndexName(Match.AWAY_TEAM_INDEX)
                .withConsistentRead(false)
                .withKeyConditionExpression("homeTeam = :v1")
                .withExpressionAttributeValues(eav);

        List<Match> awayEvents = mapper.query(Match.class, awayQuery);

        List<Match> allEvents = new LinkedList<>();
        allEvents.addAll(homeEvents);
        allEvents.addAll(awayEvents);
        allEvents.sort( (e1, e2) -> e1.getMatchDate() <= e2.getMatchDate() ? -1 : 1 );
        return allEvents;
    }

    @Override
    public Optional<Match> findMatchByTeamAndDate(String team, Long matchDate) {
        Match event = mapper.load(Match.class, team, matchDate);
        return Optional.ofNullable(event);
    }

    @Override
    public void saveOrUpdateMatch(Match event) {
        mapper.save(event);
    }

    @Override
    public void deleteMatch(String team, Long matchDate) {
        Optional<Match> oEvent = findMatchByTeamAndDate(team, matchDate);
        if (oEvent.isPresent()) {
            mapper.delete(oEvent.get());
        }
        else {
            log.error("Could not delete event, no such team and date combination");
            throw new IllegalArgumentException("Delete failed for nonexistent event");
        }
    }

    public static MatchDaoImpl instance() {

        if (instance == null) {
            synchronized(MatchDaoImpl.class) {
                if (instance == null)
                    instance = new MatchDaoImpl();
            }
        }
        return instance;
    }
}
