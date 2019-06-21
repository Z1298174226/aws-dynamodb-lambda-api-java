package com.amazonaws.handles;

import com.amazonaws.GatewayResponse;
import com.amazonaws.dao.MatchDao;
import com.amazonaws.dao.MatchDaoImpl;
import com.amazonaws.pojo.Match;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindAllMatches implements RequestHandler<Object, Object> {

    private static final Logger log = Logger.getLogger(FindAllMatches.class);

    private static final MatchDao eventDao = MatchDaoImpl.instance();

    @Override
    public Object handleRequest(Object o, Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        List<Match> events = new ArrayList<>();
        events.addAll(eventDao.findAllMatches());
        log.info("Found " + events.size() + " total events.");
        StringBuilder sb = new StringBuilder();
        for (Match match : events) {
            sb.append(match + "\n");
        }
        String output = String.format("{ \"Match\": \"%s\"}", sb.toString());
        return new GatewayResponse(output, headers, 200);
    }
}
