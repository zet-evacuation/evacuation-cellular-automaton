package org.zet.cellularautomaton.algorithm.computation;

import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.parameter.ParameterSet;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class RimeaComputationNoReactionTime extends RimeaComputation {

    public RimeaComputationNoReactionTime(PropertyAccess es, ParameterSet parameterSet) {
        super(es, parameterSet);
    }

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
