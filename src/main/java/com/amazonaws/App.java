package com.amazonaws;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.dao.MatchDao;
import com.amazonaws.dao.MatchDaoImpl;
import com.amazonaws.pojo.Match;
import com.amazonaws.pojo.Team;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.util.Consts;
import org.apache.log4j.Logger;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(App.class);

    private static final MatchDao eventDao = MatchDaoImpl.instance();

    public Object handleRequest(final Object input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        try {
            final String pageContents = this.getPageContents("https://checkip.amazonaws.com");
            String output = String.format("{ \"message\": \"hello world\", \"location\": \"%s\" }", pageContents);
            return new GatewayResponse(output, headers, 200);
        } catch (IOException e) {
            return new GatewayResponse("{}", headers, 500);
        }
    }

//    public Object findAllMatches(final Object input, final Context context) {
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("X-Custom-Header", "application/json");
//        List<Match> events = new ArrayList<>();
//        events.addAll(eventDao.findAllMatches());
//        log.info("Found " + events.size() + " total events.");
//        StringBuilder sb = new StringBuilder();
//        for (Match match : events) {
//            sb.append(match + "\n");
//        }
//        String output = String.format("{ \"Match\": \"%s\"}", sb.toString());
//        return new GatewayResponse(output, headers, 200);
//    }


//    public Object getEventsForTeam(final Object input, final Context context) throws UnsupportedEncodingException {
//        Team team = (Team) input;
//        if (null == team || team.getTeamName().isEmpty() || team.getTeamName().equals(Consts.UNDIFINED)) {
//            log.error("GetEventsForTeam received null or empty team name");
//            throw new IllegalArgumentException("Team name cannot be null or empty");
//        }
//        String name = URLDecoder.decode(team.getTeamName(), "UTF-8");
//        log.info("GetEventsForTeam invoked for team with name = " + name);
//        List<Match> events = new ArrayList<>();
//        events.addAll(eventDao.findMatchByTeam(name));
//        log.info("Found " + events.size() + " events for team = " + name);
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        headers.put("X-Custom-Header", "application/json");
//        StringBuilder sb = new StringBuilder();
//        for (Match match : events) {
//            sb.append(match + " ");
//        }
//        String output = String.format("{ \"Match\": \"%s\"}", sb.toString());
//        return new GatewayResponse(output, headers, 200);
//    }


//    public void saveOrUpdateEvent(final Object input, final Context context) {
//        Match event = (Match) input;
//        if (null == event) {
//            log.error("SaveEvent received null input");
//            throw new IllegalArgumentException("Cannot save null object");
//        }
//
//        log.info("Saving or updating event for team = " + event.getHomeTeam() + " , date = " + event.getMatchDate());
//        eventDao.saveOrUpdateMatch(event);
//        log.info("Successfully saved/updated event");
//    }

    public void deleteEvent(final Object input, final Context context) {
        Match event = (Match) input;
        if (null == event) {
            log.error("DeleteEvent received null input");
            throw new IllegalArgumentException("Cannot delete null object");
        }

        log.info("Deleting event for team = " + event.getHomeTeam() + " , date = " + event.getMatchDate());
        eventDao.deleteMatch(event.getHomeTeam(), event.getMatchDate());
        log.info("Successfully deleted event");
    }


    private String getPageContents(String address) throws IOException {
        URL url = new URL(address);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
