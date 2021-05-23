package ru.spbstu.datacollector.collector;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@RestController
public class Collector
{
  public static final Gson GSON = new Gson();
  public static final String TIMETABLE_JSON_DEFAULT_PATH = "timetables/timetable.json";

  public Collector()
  {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8001/timetable/generate";
    ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
    String timetable = responseEntity.getBody();

    try (FileWriter fileWriter = new FileWriter(TIMETABLE_JSON_DEFAULT_PATH))
    {
      assert timetable != null;
      fileWriter.write(timetable);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  @GetMapping("/show/JSONfile")
  public ResponseEntity<String> showJson()
  {
    return showJson(TIMETABLE_JSON_DEFAULT_PATH);
  }

  @GetMapping("/show/JSONfile/custom")
  public ResponseEntity<String> showJson(@RequestParam(value = "fileName", defaultValue = TIMETABLE_JSON_DEFAULT_PATH) String fileName)
  {
    try
    {
      FileReader fileReader = new FileReader(fileName);
      JsonElement jsonElement = GSON.fromJson(fileReader, JsonElement.class);
      return new ResponseEntity<>(GSON.toJson(jsonElement), HttpStatus.OK);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
  }

  @PostMapping(value = "/result", consumes = "application/json", produces = "application/json")
  public ResponseEntity<String> saveResult(@RequestBody String result)
  {
    try (FileWriter fileWriter = new FileWriter("timetables/result.json"))
    {
      assert result != null;
      fileWriter.write(result);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
