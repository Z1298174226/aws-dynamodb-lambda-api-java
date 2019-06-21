package com.amazonaws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.amazonaws.handles.FindAllMatches;
import com.amazonaws.handles.GetEventsForTeam;
import com.amazonaws.handles.SaveOrUpdateEvent;
import com.amazonaws.pojo.Match;
import com.amazonaws.pojo.Team;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AppTest {
  @Test
  public void getAllMatches() {
    FindAllMatches app = new FindAllMatches();
    GatewayResponse result = (GatewayResponse) app.handleRequest(null, null);
    assertEquals(result.getStatusCode(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"Match\""));
    System.out.println(content);
  }

  @Test
  public void getMatchesByTeam() {
    GetEventsForTeam app = new GetEventsForTeam();
    Team america = new Team("America");
    GatewayResponse result = null;
    result = (GatewayResponse) app.handleRequest(america, null);
    assertEquals(result.getStatusCode(), 200);
    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
    String content = result.getBody();
    assertNotNull(content);
    assertTrue(content.contains("\"Match\""));
    System.out.println(content);
  }

  @Test
  public void saveOrUpdateEvent() {
    SaveOrUpdateEvent app = new SaveOrUpdateEvent();
    Match brazil = new Match("Brazil", new Long(20190603));
    try {
      app.handleRequest(brazil, null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }
}
