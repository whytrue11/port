package ru.spbstu.analyzer.entity.cranes;

import java.util.GregorianCalendar;

public class LooseCrane extends Crane
{
  private static final int liftingCapacity = 160;
  private static final int unloadingTime = 10;

  public LooseCrane(long releaseTime)
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
