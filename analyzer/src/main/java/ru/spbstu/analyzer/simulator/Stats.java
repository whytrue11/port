package ru.spbstu.analyzer.simulator;

import ru.spbstu.analyzer.entity.ship.Ship;

import java.util.GregorianCalendar;

public class Stats
{
  private final Ship ship;
  private final GregorianCalendar arrivalTime;
  private final long waitingTime;

  public Stats(Ship ship, GregorianCalendar arrivalTime, long waitingTime)
  {
    this.ship = ship;
    this.arrivalTime = arrivalTime;
    this.waitingTime = waitingTime;
  }

  public Ship getShip()
  {
    return ship;
  }

  public GregorianCalendar getArrivalTime()
  {
    return arrivalTime;
  }

  public long getWaitingTime()
  {
    return waitingTime;
  }

  @Override
  public String toString()
  {
    return "Stats{" +
            "ship=" + ship +
            ", arrivalTime=" + arrivalTime.getTime() +
            ", waitingTime=" + waitingTime / 360000 +
            '}';
  }
}
