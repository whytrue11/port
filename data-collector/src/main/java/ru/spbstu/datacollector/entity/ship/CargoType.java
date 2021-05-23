package ru.spbstu.datacollector.entity.ship;

public enum CargoType
{
  LOOSE(0),
  LIQUID(1),
  CONTAINER(2);

  public final int number;

  CargoType(int liftingCapacity)
  {
    this.number = liftingCapacity;
  }
}
