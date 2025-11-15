package org.synchronoss.cpo.cassandra.config;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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

import com.datastax.driver.core.policies.LoadBalancingPolicy;

/**
 * Factory to construct a LoadBalancingPolicy
 *
 * @author dberry
 */
public abstract class LoadBalancingPolicyFactory {

  /** Constructs a LoadBalancingPolicyFactory */
  public LoadBalancingPolicyFactory() {}

  /**
   * Get the factory method name
   *
   * @return The method name
   */
  public String getFactoryMethodName() {
    return "createLoadBalancingPolicy";
  }

  /**
   * Create the LoadBalancingPolicy
   *
   * @return The LoadBalancingPolicy
   */
  public abstract LoadBalancingPolicy createLoadBalancingPolicy();
}
