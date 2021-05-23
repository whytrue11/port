package ru.spbstu.analyzer.simulator;

import ru.spbstu.analyzer.entity.cranes.Crane;
import ru.spbstu.analyzer.entity.timetable.TimetableCell;

import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import static ru.spbstu.analyzer.simulator.Simulator.*;

class Unloader implements Callable<Stats>
{
  private final Crane crane;
  private final TimetableCell cell;
  private final long unloadingTime;
  private final Semaphore SEMAPHORE;
  private final TotalStats totalStats;

  public Unloader(Crane crane, TimetableCell cell, long unloadingTime, Semaphore semaphore, TotalStats totalStats)
  {
    this.crane = crane;
    this.cell = cell;
    this.unloadingTime = unloadingTime;
    SEMAPHORE = semaphore;
    this.totalStats = totalStats;
  }

  @Override
  public Stats call()
  {
    try
    {
      SEMAPHORE.acquire();
    } catch (InterruptedException e)
    {
      e.printStackTrace();
    }

    long waitingTime = (crane.getReleaseTime() > cell.getArrivalTime().getTimeInMillis()) ?
            crane.getReleaseTime() - cell.getArrivalTime().getTimeInMillis() : 0;

    long delay = Math.round((Math.random() * CRANE_DELAY)) * 60000;
    crane.setReleaseTime(cell.getArrivalTime().getTimeInMillis() + unloadingTime + delay);

    double craneUnloadingSpeed = crane.unloadingSpeed();

    SEMAPHORE.release();

    cell.getShip().setCargoSize((int) (cell.getShip().getCargoSize() -
            Math.floor(craneUnloadingSpeed * unloadingTime / 60000)));
    if (cell.getShip().getCargoSize() < 0)
    {
      cell.getShip().setCargoSize(0);
    }

    synchronized (totalStats)
    {
      totalStats.maxUnloadingDelay = Math.max(delay, totalStats.maxUnloadingDelay);
      totalStats.averageUnloadingDelay = totalStats.averageUnloadingDelay.add(BigDecimal.valueOf(delay));
    }

    return new Stats(cell.getShip(), cell.getArrivalTime(), waitingTime);
  }
}
