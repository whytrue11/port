package ru.spbstu.datacollector.entity.cranes;

import java.util.GregorianCalendar;

public class ContainerCrane extends Crane
{
  private static final int liftingCapacity = 1;
  private static final int unloadingTime = 2;

  public ContainerCrane(long releaseTime)
  {
    super(releaseTime);
  }

  @Override
  public int getLiftingCapacity()
  {
    return liftingCapacity;
  }

  @Override
  public int getUnloadingTime()
  {
    return unloadingTime;
  }

  @Override
  public double unloadingSpeed()
  {
    return (double) liftingCapacity / unloadingTime;
  }

}
