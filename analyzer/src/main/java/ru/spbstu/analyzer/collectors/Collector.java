package ru.spbstu.analyzer.collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.spbstu.analyzer.entity.timetable.TimetableCell;
import ru.spbstu.analyzer.simulator.Simulator;

import java.util.List;

@RestController
public class Collector
{
  public static final Gson GSON = new Gson();
  private static final int[] CRANES_COUNT = {1, 1, 1};
  private RestTemplate restTemplate;
  private final Simulator simulator;

  public Collector()
  {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8002/show/JSONfile";
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
    List<TimetableCell> timetable = GSON.fromJson(responseEntity.getBody(), new TypeToken<List<TimetableCell>>(){}.getType());
    simulator = new Simulator();
    simulator.unloadingShips(timetable, CRANES_COUNT);
  }

  @GetMapping("/statistic")
  public ResponseEntity<String> getStats()
  {
    return new ResponseEntity<>(GSON.toJson(simulator.getStats()), HttpStatus.OK);
  }

  @GetMapping("/statistic/total")
  public ResponseEntity<String> getTotalStats()
  {
    return new ResponseEntity<>(GSON.toJson(simulator.getTotalStats()), HttpStatus.OK);
  }

  @PostMapping(value = "/post/statistic", produces = "application/json")
  public ResponseEntity<String> postStatistic()
  {
    restTemplate = new RestTemplate();
    String result = getStats().getBody() + getTotalStats().getBody();
    String url = "http://localhost:8002/result";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> request = new HttpEntity<>(result, headers);
    restTemplate.postForEntity(url, request, String.class);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
