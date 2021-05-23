package ru.spbstu.datacollector.entity.cranes;

import ru.spbstu.datacollector.entity.ship.CargoType;

public abstract class Crane
{
  public static Crane createCrane(CargoType cargoType, long releaseTime) throws IllegalArgumentException
  {
    if (cargoType == CargoType.LOOSE)
    {
      return new LooseCrane(releaseTime);
    }
    if (cargoType == CargoType.LIQUID)
    {
      return new LiquidCrane(releaseTime);
    }
    if (cargoType == CargoType.CONTAINER)
    {
      return new ContainerCrane(releaseTime);
    }

    throw new IllegalArgumentException("There are no other types of cranes");
  }

  private long releaseTime;

  public Crane(long releaseTime)
  {
    this.releaseTime = releaseTime;
  }

  public abstract int getLiftingCapacity();

  public abstract int getUnloadingTime();

  public abstract double unloadingSpeed();

  public long getReleaseTime()
  {
    return releaseTime;
  }

  public void setReleaseTime(long releaseTime)
  {
    this.releaseTime = releaseTime;
  }
}
