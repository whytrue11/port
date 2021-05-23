package ru.spbstu.datacollector.entity.ship;

import ru.spbstu.datacollector.entity.timetable.TimetableCell;

public class Ship implements Cloneable
{
  private String name;
  private final CargoType cargoType;
  private int cargoSize;

  public Ship(String name, CargoType cargoType, int cargoSize)
  {
    this.name = name;
    this.cargoType = cargoType;
    this.cargoSize = cargoSize;
  }

  public String getName()
  {
    return name;
  }

  public CargoType getCargoType()
  {
    return cargoType;
  }

  public int getCargoSize()
  {
    return cargoSize;
  }
  public void setName(String name)
  {
    this.name = name;
  }

  public void setCargoSize(int cargoSize)
  {
    this.cargoSize = cargoSize;
  }

  @Override
  public Ship clone()
  {
    return new Ship(name, cargoType, cargoSize);
  }

  @Override
  public String toString()
  {
    return "Ship{" +
            "name='" + name + '\'' +
            ", cargoType=" + cargoType +
            ", cargoSize=" + cargoSize +
            '}';
  }
}
