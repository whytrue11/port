package ru.spbstu.analyzer.simulator;

import ru.spbstu.analyzer.entity.ship.CargoType;

import java.math.BigDecimal;
import java.util.Arrays;

public class TotalStats implements Cloneable
{
  protected int shipCount;
  protected BigDecimal averageWaitingTime;
  protected long maxUnloadingDelay;
  protected BigDecimal averageUnloadingDelay;
  protected long totalFineAmount;

  protected int[] requiredNumberOfCranes;
  protected long cranesPriceAndFineAmount;
  protected long[] fineAmounts;

  public TotalStats()
  {
    averageWaitingTime = new BigDecimal(0);
    averageUnloadingDelay = new BigDecimal(0);
    requiredNumberOfCranes = new int[CargoType.values().length];
    fineAmounts = new long[CargoType.values().length];
  }

  public int getShipCount()
  {
    return shipCount;
  }

  public BigDecimal getAverageWaitingTime()
  {
    return averageWaitingTime;
  }

  public long getMaxUnloadingDelay()
  {
    return maxUnloadingDelay;
  }

  public BigDecimal getAverageUnloadingDelay()
  {
    return averageUnloadingDelay;
  }

  public long getTotalFineAmount()
  {
    return totalFineAmount;
  }

  public int[] getRequiredNumberOfCranes()
  {
    return requiredNumberOfCranes;
  }

  @Override
  public TotalStats clone()
  {
    TotalStats result = new TotalStats();
    result.shipCount = shipCount;
    result.averageWaitingTime = averageWaitingTime;
    result.maxUnloadingDelay = maxUnloadingDelay;
    result.averageUnloadingDelay = averageUnloadingDelay;
    result.totalFineAmount = totalFineAmount;
    result.requiredNumberOfCranes = new int[requiredNumberOfCranes.length];
    result.cranesPriceAndFineAmount = cranesPriceAndFineAmount;
    result.fineAmounts = new long[fineAmounts.length];
    System.arraycopy(requiredNumberOfCranes, 0, result.requiredNumberOfCranes, 0, requiredNumberOfCranes.length);
    System.arraycopy(fineAmounts, 0, result.fineAmounts, 0, fineAmounts.length);
    return result;
  }

  @Override
  public String toString()
  {
    return "TotalStats{" +
            "shipCount=" + shipCount +
            ", averageWaitingTime=" + averageWaitingTime +
            ", maxUnloadingDelay=" + maxUnloadingDelay +
            ", averageUnloadingDelay=" + averageUnloadingDelay +
            ", totalFineAmount=" + totalFineAmount +
            ", requiredNumberOfCranes=" + Arrays.toString(requiredNumberOfCranes) +
            ", cranesPriceAndFineAmount=" + cranesPriceAndFineAmount +
            ", fineAmounts=" + Arrays.toString(fineAmounts) +
            '}';
  }
}
