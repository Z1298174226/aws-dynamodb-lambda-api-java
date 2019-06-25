package com.amazonaws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.amazonaws.handles.FindAllMatches;
import com.amazonaws.handles.SaveOrUpdateEvent;
import com.amazonaws.pojo.Match;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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


//  @Ignore
//  @Test
//  public void getMatchesByTeam() {
//    GetEventsForTeam app = new GetEventsForTeam();
//    Team america = new Team("America");
//    GatewayResponse result = null;
//    result = (GatewayResponse) app.handleRequest(america, null);
//    assertEquals(result.getStatusCode(), 200);
//    assertEquals(result.getHeaders().get("Content-Type"), "application/json");
//    String content = result.getBody();
//    assertNotNull(content);
//    assertTrue(content.contains("\"Match\""));
//    System.out.println(content);
//  }

  @Ignore
  @Test
  public void saveOrUpdateEvent() {
    SaveOrUpdateEvent app = new SaveOrUpdateEvent();
    Match brazil = new Match("England", new Long(20190614));
    try {
      app.handleRequest(brazil, null);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }

  @Ignore
  @Test
  public void testApi() {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:4567/restapis/9953722700/test/_user_request_/German";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    System.out.println(response);
  }

}
