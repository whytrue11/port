package ru.spbstu.analyzer.simulator;

import ru.spbstu.analyzer.entity.ship.CargoType;
import ru.spbstu.analyzer.entity.ship.Ship;
import ru.spbstu.analyzer.entity.timetable.TimetableCell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Generator
{
  public static final int MAX_CONTAINERS_COUNT = 21413;
  public static final int MIN_CONTAINERS_COUNT = 260;

  public static final int MAX_WEIGHT = 365000;
  public static final int MIN_WEIGHT = 1000;

  public static List<TimetableCell> generateTimetable(
          GregorianCalendar startDateGeneration, int durationDateGeneration, int shipsCount)
  {
    List<TimetableCell> timetable = new ArrayList<>();
    for (int i = 0; i < shipsCount; i++)
    {
      timetable.add(generateTimetableCell(startDateGeneration, durationDateGeneration));
    }
    return timetable;
  }

  public static TimetableCell generateTimetableCell(GregorianCalendar startDateGeneration, int durationDateGeneration)
  {
    return new TimetableCell(generateShip(), generateDate(startDateGeneration, durationDateGeneration));
  }

  public static Ship generateShip()
  {
    CargoType cargoType = generateCargoType();
    return new Ship(generateName(), cargoType, generateCargoSize(cargoType));
  }

  public static String generateName()
  {
    int length = (int) (Math.random() * 8 + 3);
    StringBuilder name = new StringBuilder();

    name.append(Character.toUpperCase((char) ((int) (Math.random() * 26 + 97))));
    --length;
    for (int i = 0; i < length; ++i)
    {
      name.append((char) ((int) (Math.random() * 26 + 97)));
    }
    return name.toString();
  }

  public static GregorianCalendar generateDate(GregorianCalendar startDateGeneration, int durationDateGeneration)
  {
    ++durationDateGeneration;
    GregorianCalendar result = (GregorianCalendar) startDateGeneration.clone();

    result.add(Calendar.DAY_OF_YEAR, (int) (Math.random() * durationDateGeneration));
    result.add(Calendar.HOUR, (int) (Math.random() * 24));
    result.add(Calendar.MINUTE, (int) (Math.random() * 60));

    return result;
  }

  public static CargoType generateCargoType()
  {
    return CargoType.values()[(int) (Math.random() * 10) % CargoType.values().length];
  }

  public static int generateCargoSize(CargoType cargoType)
  {
    int size = 0;

    if (cargoType == CargoType.CONTAINER)
    {
      size = (int) (Math.random() * (MAX_CONTAINERS_COUNT - MIN_CONTAINERS_COUNT) + MIN_CONTAINERS_COUNT);
    }
    else
    {
      size = (int) (Math.random() * (MAX_WEIGHT - MIN_WEIGHT) + MIN_WEIGHT);
    }

    return size;
  }
}
