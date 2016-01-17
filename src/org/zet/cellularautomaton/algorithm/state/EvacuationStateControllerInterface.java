package org.zet.cellularautomaton.algorithm.state;

import org.zet.cellularautomaton.DeathCause;
import org.zet.cellularautomaton.Individual;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationStateControllerInterface {

    public void die(Individual i, DeathCause cause);

}
