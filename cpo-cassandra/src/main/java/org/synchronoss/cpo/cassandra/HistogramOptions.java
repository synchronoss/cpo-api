package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import java.util.List;

/**
 * Histogram tuning shared by the driver's per-metric latency options (session cql-requests, session
 * throttling.delay, node cql-messages).
 *
 * @author dberry
 */
public class HistogramOptions {

  /** Constructs an empty HistogramOptions with all tuning values unset. */
  public HistogramOptions() {}

  private Long highestLatencyMillis;
  private Long lowestLatencyMillis;
  private Integer significantDigits;
  private Long refreshIntervalMinutes;
  private List<Long> sloMillis;
  private List<Double> publishPercentiles;

  /**
   * Get the largest latency expected to be recorded, in milliseconds
   *
   * @return the highest latency in milliseconds
   */
  public Long getHighestLatencyMillis() {
    return highestLatencyMillis;
  }

  /**
   * Set the largest latency expected to be recorded, in milliseconds
   *
   * @param highestLatencyMillis the highest latency in milliseconds
   */
  public void setHighestLatencyMillis(Long highestLatencyMillis) {
    this.highestLatencyMillis = highestLatencyMillis;
  }

  /**
   * Get the shortest latency expected to be recorded, in milliseconds
   *
   * @return the lowest latency in milliseconds
   */
  public Long getLowestLatencyMillis() {
    return lowestLatencyMillis;
  }

  /**
   * Set the shortest latency expected to be recorded, in milliseconds
   *
   * @param lowestLatencyMillis the lowest latency in milliseconds
   */
  public void setLowestLatencyMillis(Long lowestLatencyMillis) {
    this.lowestLatencyMillis = lowestLatencyMillis;
  }

  /**
   * Get the number of significant decimal digits of resolution
   *
   * @return the number of significant digits
   */
  public Integer getSignificantDigits() {
    return significantDigits;
  }

  /**
   * Set the number of significant decimal digits of resolution
   *
   * @param significantDigits the number of significant digits
   */
  public void setSignificantDigits(Integer significantDigits) {
    this.significantDigits = significantDigits;
  }

  /**
   * Get the interval at which percentile data is refreshed, in minutes
   *
   * @return the refresh interval in minutes
   */
  public Long getRefreshIntervalMinutes() {
    return refreshIntervalMinutes;
  }

  /**
   * Set the interval at which percentile data is refreshed, in minutes
   *
   * @param refreshIntervalMinutes the refresh interval in minutes
   */
  public void setRefreshIntervalMinutes(Long refreshIntervalMinutes) {
    this.refreshIntervalMinutes = refreshIntervalMinutes;
  }

  /**
   * Get the service-level-objective latency boundaries, in milliseconds
   *
   * @return the SLO boundaries in milliseconds
   */
  public List<Long> getSloMillis() {
    return sloMillis;
  }

  /**
   * Set the service-level-objective latency boundaries, in milliseconds
   *
   * @param sloMillis the SLO boundaries in milliseconds
   */
  public void setSloMillis(List<Long> sloMillis) {
    this.sloMillis = sloMillis;
  }

  /**
   * Get the percentiles to publish as their own time series
   *
   * @return the percentiles to publish
   */
  public List<Double> getPublishPercentiles() {
    return publishPercentiles;
  }

  /**
   * Set the percentiles to publish as their own time series
   *
   * @param publishPercentiles the percentiles to publish
   */
  public void setPublishPercentiles(List<Double> publishPercentiles) {
    this.publishPercentiles = publishPercentiles;
  }
}
