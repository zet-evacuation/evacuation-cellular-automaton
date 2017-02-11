package org.zet.cellularautomaton.algorithm;

import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationStepCompleteEvent extends AlgorithmProgressEvent<EvacuationSimulationProblem, EvacuationSimulationResult> {

    public EvacuationStepCompleteEvent(Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> algorithm,
            double progress) {
        super(algorithm, progress);
    }

}
