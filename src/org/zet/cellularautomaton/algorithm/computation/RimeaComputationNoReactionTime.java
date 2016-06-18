package org.zet.cellularautomaton.algorithm.computation;

import org.zet.cellularautomaton.Individual;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RimeaComputationNoReactionTime extends RimeaComputation {

    /**
     *
     * @param individual
     * @return 0.0
     */
    @Override
    public double idleThreshold(Individual individual) {
        return 0.0;
    }

}
