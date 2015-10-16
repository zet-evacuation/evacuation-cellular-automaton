/**
 * Neighborhood.java
 * Created: 25.10.2012, 14:51:05
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.zetool.simulation.cellularautomaton;

import java.util.Collection;

/**
 * @param <St> state
 * @param <Ce>
 * @author Jan-Philipp Kappmeier
 */
public interface Neighborhood<Ce extends Cell<Ce,?>> {
	Collection<Ce> getNeighbors( Ce cell );
}
