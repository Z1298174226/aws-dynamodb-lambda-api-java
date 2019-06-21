package com.amazonaws.handles;

import com.amazonaws.GatewayResponse;
import com.amazonaws.dao.MatchDao;
import com.amazonaws.dao.MatchDaoImpl;
import com.amazonaws.pojo.Match;
import com.amazonaws.pojo.Team;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Consts;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetEventsForTeam implements RequestHandler<Object, Object> {
    private static final Logger log = Logger.getLogger(GetEventsForTeam.class);

    private static final MatchDao eventDao = MatchDaoImpl.instance();

    @Override
    public Object handleRequest(Object input, Context context) {
        Team team = (Team) input;
        if (null == team || team.getTeamName().isEmpty() || team.getTeamName().equals(Consts.UNDIFINED)) {
            log.error("GetEventsForTeam received null or empty team name");
            throw new IllegalArgumentException("Team name cannot be null or empty");
        }
        String name = null;
        try {
            name = URLDecoder.decode(team.getTeamName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("GetEventsForTeam invoked for team with name = " + name);
        List<Match> events = new ArrayList<>();
        events.addAll(eventDao.findMatchByTeam(name));
        log.info("Found " + events.size() + " events for team = " + name);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        StringBuilder sb = new StringBuilder();
        for (Match match : events) {
            sb.append(match + " ");
        }
        String output = String.format("{ \"Match\": \"%s\"}", sb.toString());
        return new GatewayResponse(output, headers, 200);
    }
}
