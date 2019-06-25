package com.amazonaws.handles;

import com.amazonaws.GatewayResponse;
import com.amazonaws.dao.MatchDao;
import com.amazonaws.dao.MatchDaoImpl;
import com.amazonaws.pojo.Match;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SaveOrUpdateEvent implements RequestHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(SaveOrUpdateEvent.class);

    private static final MatchDao eventDao = MatchDaoImpl.instance();


    @Override
    public Object handleRequest(Object input, Context context) {
        JSONObject jsonObject = JSONObject.fromObject(input);
        String homeTeam = jsonObject.getJSONObject("pathParameters").getString("homeTeam");
        Long eventDate = Long.valueOf(jsonObject.getJSONObject("pathParameters").getString("eventDate"));
        Match event = new Match(homeTeam, eventDate);
        if (null == event) {
            log.error("SaveEvent received null input");
            throw new IllegalArgumentException("Cannot save null object" + jsonObject.toString());
        }
        log.info("Saving or updating event for team = " + event.getHomeTeam() + " , date = " + event.getMatchDate());
        eventDao.saveOrUpdateMatch(event);
        log.info("Successfully saved/updated event");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        String output = String.format("Save Success");
        return new GatewayResponse(output, headers, 200);
    }
}
