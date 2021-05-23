package ru.spbstu.analyzer.entity.timetable;

import ru.spbstu.analyzer.entity.cranes.Crane;
import ru.spbstu.analyzer.entity.ship.CargoType;
import ru.spbstu.analyzer.entity.ship.Ship;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class TimetableCell implements Cloneable
{
  private Ship ship;
  private GregorianCalendar arrivalTime;
  private long parkingTime; //milliseconds

  private static final List<Crane> cranes = new ArrayList<>();
  static
  {
    for (CargoType cargoType: CargoType.values())
    {
      cranes.add(Crane.createCrane(cargoType, 0));
    }
  }

  public TimetableCell(Ship ship, GregorianCalendar arrivalTime)
  {
    this.ship = ship;
    this.arrivalTime = arrivalTime;
    parkingTime = (long) Math.ceil(ship.getCargoSize() / cranes.get(ship.getCargoType().number).unloadingSpeed()) * 60000;
  }

  public Ship getShip()
  {
    return ship;
  }

  public GregorianCalendar getArrivalTime()
  {
    return arrivalTime;
  }

  public long getParkingTime()
  {
    return parkingTime;
  }

  public void setShip(Ship ship)
  {
    this.ship = ship;
  }

  public void setArrivalTime(GregorianCalendar arrivalTime)
  {
    this.arrivalTime = arrivalTime;
  }

  public void setParkingTime(long parkingTime)
  {
    this.parkingTime = parkingTime;
  }

  @Override
  public TimetableCell clone()
  {
    return new TimetableCell(ship.clone(), (GregorianCalendar) arrivalTime.clone());
  }

  @Override
  public String toString()
  {
    return "TimetableCell{" +
            "ship=" + ship +
            ", arrivalTime=" + arrivalTime.getTime() +
            ", parkingTime=" + parkingTime +
            '}';
  }
}
