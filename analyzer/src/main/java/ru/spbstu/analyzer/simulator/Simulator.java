package ru.spbstu.analyzer.simulator;

import ru.spbstu.analyzer.entity.cranes.Crane;
import ru.spbstu.analyzer.entity.ship.CargoType;
import ru.spbstu.analyzer.entity.timetable.TimetableCell;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.math.RoundingMode.*;
import static ru.spbstu.analyzer.simulator.Generator.generateDate;

public class Simulator
{
  private static final int ARRIVAL_TIME_DELAY_FROM = -7; //days
  private static final int ARRIVAL_TIME_DELAY_TO = 7;
  private static final long MILLISECONDS_IN_HOUR = 3600000L;
  private static final int FINE_PER_HOUR = 100;
  private static final int CRANE_PRICE = 30000;
  protected static final int CRANE_DELAY = 1440; //minutes

  private final Semaphore SEMAPHORE;

  private List<Stats> stats;
  private volatile TotalStats totalStats;

  public Simulator()
  {
    SEMAPHORE = new Semaphore(2);
    stats = new ArrayList<>();
    totalStats = new TotalStats();
  }

  public List<Stats> getStats()
  {
    return stats;
  }

  public TotalStats getTotalStats()
  {
    return totalStats;
  }

  public void unloadingShips(List<TimetableCell> timetable, int[] cranesCount)
  {
    List<List<TimetableCell>> shipQueues = createLiveQueues(timetable);

    CargoType[] cargoTypes = CargoType.values();
    long[] startDates = new long[cargoTypes.length];
    for (int i = 0; i < cargoTypes.length; i++)
    {
      startDates[i] = shipQueues.get(i).isEmpty() ? 0 : shipQueues.get(i).get(0).getArrivalTime().getTimeInMillis();
    }
    List<NavigableMap<Long, Crane>> cranes = createCranes(cranesCount, startDates);

    //Clone
    List<List<TimetableCell>> clonedShipQueues = new ArrayList<>();
    for (int i = 0; i < cargoTypes.length; ++i)
    {
      clonedShipQueues.add(shipQueues.get(i).stream().map(TimetableCell::clone).collect(Collectors.toList()));
    }

    //Analyze
    totalStats.shipCount = timetable.size();
    this.stats = collectStatistics(clonedShipQueues, cranes);

    TotalStats cloneTotalStats = totalStats.clone();

    for (int i = 0; i < cargoTypes.length; ++i)
    {
      //Calculating the required number of cranes
      long fineAmount = totalStats.fineAmounts[i];
      totalStats.fineAmounts[0] = 0;
      long curFineAmount = Long.MAX_VALUE;
      long cranesPrice = 0;

      if (fineAmount < CRANE_PRICE)
      {
        cloneTotalStats.requiredNumberOfCranes[i] = cranes.get(i).size();
        continue;
      }

      int optimalRequiredNumberOfCranes = 0;
      long minFineAmount = Long.MAX_VALUE;

      int cranesSize = cranes.get(i).size();
      int startCranesCount = cranesSize;
      while (curFineAmount != 0)
      {
        ++cranesSize;
        cranesPrice += CRANE_PRICE;
        cranes.get(0).clear();
        for (int j = 0; j < cranesSize; ++j)
        {
          cranes.get(0).put(startDates[i], Crane.createCrane(cargoTypes[i], startDates[i]));
        }

        clonedShipQueues.clear();
        clonedShipQueues.add(shipQueues.get(i).stream().map(TimetableCell::clone).collect(Collectors.toList()));

        collectStatistics(clonedShipQueues, cranes);
        curFineAmount = totalStats.fineAmounts[0];

        if (curFineAmount + cranesPrice < minFineAmount + (long) optimalRequiredNumberOfCranes * CRANE_PRICE)
        {
          minFineAmount = curFineAmount;
          optimalRequiredNumberOfCranes = cranesSize;
        }

        totalStats.fineAmounts[0] = 0;
      }

      cloneTotalStats.requiredNumberOfCranes[i] =
              minFineAmount < fineAmount ? optimalRequiredNumberOfCranes : startCranesCount;
      cloneTotalStats.cranesPriceAndFineAmount +=
              Math.min(minFineAmount + (long) optimalRequiredNumberOfCranes * CRANE_PRICE, fineAmount);
    }

    totalStats = cloneTotalStats;
  }

  private List<NavigableMap<Long, Crane>> createCranes(int[] cranesCount, long[] startDates)
  {
    CargoType[] cargoTypes = CargoType.values();

    List<NavigableMap<Long, Crane>> cranes = new ArrayList<>();
    for (int i = 0; i < cargoTypes.length; ++i)
    {
      cranes.add(new TreeMap<>((Long a, Long b) ->
      {
        if (a < b)
        {
          return -1;
        }
        else
        {
          return 1;
        }
      }));

      try
      {
        for (int j = 0; j < cranesCount[i]; ++j)
        {
          cranes.get(i).put(startDates[i], Crane.createCrane(cargoTypes[i], startDates[i]));
        }
      } catch (IllegalArgumentException e)
      {
        e.printStackTrace();
      }
    }

    return cranes;
  }

  private List<List<TimetableCell>> createLiveQueues(List<TimetableCell> timetable)
  {
    CargoType[] cargoTypes = CargoType.values();
    List<List<TimetableCell>> shipQueues = new ArrayList<>();
    for (int i = 0; i < cargoTypes.length; ++i)
    {
      shipQueues.add(new ArrayList<>());
    }

    timetable.forEach(cell ->
    {
      //Add arrival time delay
      GregorianCalendar arrivalTimeDelay = cell.getArrivalTime();
      arrivalTimeDelay.add(Calendar.DAY_OF_YEAR, ARRIVAL_TIME_DELAY_FROM);
      arrivalTimeDelay = generateDate(arrivalTimeDelay, Math.abs(ARRIVAL_TIME_DELAY_TO - ARRIVAL_TIME_DELAY_FROM));
      cell.setArrivalTime(arrivalTimeDelay);

      //Add ships in live queue
      for (CargoType cargoType : cargoTypes)
      {
        if (cell.getShip().getCargoType() == cargoType)
        {
          shipQueues.get(cargoType.number).add(cell);
        }
      }
    });

    //Live queue sort
    for (int i = 0; i < cargoTypes.length; ++i)
    {
      shipQueues.get(i).sort(Comparator.comparing(TimetableCell::getArrivalTime));
    }
    return shipQueues;
  }

  private List<Stats> collectStatistics(List<List<TimetableCell>> shipQueues, List<NavigableMap<Long, Crane>> cranes)
  {
    List<Future<Stats>> futures = new ArrayList<>();

    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    for (int i = 0; i < shipQueues.size(); ++i)
    {
      int finalCraneType = i;
      shipQueues.get(i).forEach((ship) ->
      {
        Crane crane1 = null;
        Crane crane2 = null;

        crane1 = cranes.get(finalCraneType).pollFirstEntry().getValue();

        long releaseTimeCrane2 = (!cranes.get(finalCraneType).isEmpty()) ?
                cranes.get(finalCraneType).firstEntry().getValue().getReleaseTime() : Long.MAX_VALUE;

        Future<Stats> statsFuture = null;
        if (releaseTimeCrane2 < ship.getArrivalTime().getTimeInMillis() + ship.getParkingTime())
        {
          long releaseTimeCrane1 = crane1.getReleaseTime();
          crane2 = cranes.get(finalCraneType).pollFirstEntry().getValue();

          int cargoSizeAfterCrane1Unload = (int) (ship.getShip().getCargoSize() -
                  ((releaseTimeCrane2 - releaseTimeCrane1) * crane1.unloadingSpeed() / 60000));
          long workingTimeTogether = (long) Math.ceil(cargoSizeAfterCrane1Unload / crane1.unloadingSpeed()) * 60000 / 2;

          statsFuture = executor.submit(new Unloader(
                  crane1, ship, releaseTimeCrane2 - releaseTimeCrane1 + workingTimeTogether, SEMAPHORE, totalStats));
          executor.submit(new Unloader(crane2, ship, workingTimeTogether, SEMAPHORE, totalStats));

        }
        else
        {
          statsFuture = executor.submit(new Unloader(crane1, ship, ship.getParkingTime(), SEMAPHORE, totalStats));
        }
        futures.add(statsFuture);

        try
        {
          SEMAPHORE.acquire(2);
        } catch (InterruptedException e)
        {
          e.printStackTrace();
        }

        cranes.get(finalCraneType).put(crane1.getReleaseTime(), crane1);
        if (crane2 != null)
        {
          cranes.get(finalCraneType).put(crane2.getReleaseTime(), crane2);
        }

        SEMAPHORE.release(2);
      });
    }
    executor.shutdown();

    //Stats collection
    List<Stats> statsList = new ArrayList<>();
    try
    {
      int k = 0;
      for (int i = 0; i < shipQueues.size(); ++i)
      {
        for (int j = 0; j < shipQueues.get(i).size(); ++j, ++k)
        {
          Stats stats = futures.get(k).get();
          statsList.add(stats);

          if (stats.getWaitingTime() != 0)
          {
            totalStats.fineAmounts[i] += stats.getWaitingTime() / MILLISECONDS_IN_HOUR * FINE_PER_HOUR;
            totalStats.averageWaitingTime = totalStats.averageWaitingTime
                    .add(BigDecimal.valueOf(stats.getWaitingTime()));
          }
        }
        totalStats.totalFineAmount += totalStats.fineAmounts[i];
      }
    } catch (InterruptedException | ExecutionException e)
    {
      e.printStackTrace();
    }

    totalStats.averageWaitingTime = totalStats.averageWaitingTime
            .divide(BigDecimal.valueOf(totalStats.shipCount * MILLISECONDS_IN_HOUR), HALF_EVEN);

    totalStats.maxUnloadingDelay /= MILLISECONDS_IN_HOUR;

    totalStats.averageUnloadingDelay = totalStats.averageUnloadingDelay
            .divide(BigDecimal.valueOf(totalStats.shipCount * MILLISECONDS_IN_HOUR), HALF_EVEN);
    return statsList;
  }
}
