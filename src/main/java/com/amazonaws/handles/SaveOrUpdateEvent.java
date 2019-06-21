package com.amazonaws.handles;

import com.amazonaws.dao.MatchDao;
import com.amazonaws.dao.MatchDaoImpl;
import com.amazonaws.pojo.Match;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.log4j.Logger;

public class SaveOrUpdateEvent implements RequestHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(SaveOrUpdateEvent.class);

    private static final MatchDao eventDao = MatchDaoImpl.instance();

    @Override
    public Object handleRequest(Object input, Context context) {
        Match event = (Match) input;
        if (null == event) {
            log.error("SaveEvent received null input");
            throw new IllegalArgumentException("Cannot save null object");
        }
        log.info("Saving or updating event for team = " + event.getHomeTeam() + " , date = " + event.getMatchDate());
        eventDao.saveOrUpdateMatch(event);
        log.info("Successfully saved/updated event");
        return null;
    }
}
