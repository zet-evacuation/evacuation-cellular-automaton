package org.zet.cellularautomaton.algorithm;

import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationInitializationCompleteEvent extends AlgorithmProgressEvent<EvacuationSimulationProblem, EvacuationSimulationResult> {

    public EvacuationInitializationCompleteEvent(Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> algorithm) {
        super(algorithm, 0);
    }

}
