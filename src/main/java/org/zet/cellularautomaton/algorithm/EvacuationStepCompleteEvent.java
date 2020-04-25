package org.zet.cellularautomaton.algorithm;

import java.util.Collections;
import java.util.List;
import org.zet.cellularautomaton.results.Action;
import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.algorithm.AlgorithmProgressEvent;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class EvacuationStepCompleteEvent extends AlgorithmProgressEvent<EvacuationSimulationProblem, EvacuationSimulationResult> {

    private final List<Action> initializationActions;

    public EvacuationStepCompleteEvent(Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> algorithm,
            double progress, List<Action> initializationActions) {
        super(algorithm, progress);
        this.initializationActions = initializationActions;
    }

    public List<Action> getInitializationActions() {
        return Collections.unmodifiableList(initializationActions);
    }

}
