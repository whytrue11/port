package ru.spbstu.generator.collectors;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.generator.simulator.Generator;

import java.util.Calendar;
import java.util.GregorianCalendar;

@RestController
public class Collector
{
  private static final GregorianCalendar START_DATE_GENERATION = new GregorianCalendar(2021, Calendar.JULY, 1);
  private static final Gson GSON = new Gson();
  private static final int DURATION_DATE_GENERATION = 30;
  private static final int SHIPS_COUNT = 1000;
  private String timetable;

  @GetMapping("/timetable/generate")
  public String generateTimetable()
  {
    return timetable = GSON.toJson(Generator.generateTimetable(START_DATE_GENERATION, DURATION_DATE_GENERATION, SHIPS_COUNT));
  }

  @GetMapping("/timetable")
  public String getTimetable()
  {
    return timetable;
  }
}
