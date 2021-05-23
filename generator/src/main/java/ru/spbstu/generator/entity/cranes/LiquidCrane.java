package ru.spbstu.generator.entity.cranes;

import java.util.GregorianCalendar;

public class LiquidCrane extends Crane
{
  private static final int liftingCapacity = 150;
  private static final int unloadingTime = 10;

  public LiquidCrane(long releaseTime)
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
