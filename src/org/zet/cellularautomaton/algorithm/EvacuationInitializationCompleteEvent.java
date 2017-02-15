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
public class EvacuationInitializationCompleteEvent extends AlgorithmProgressEvent<EvacuationSimulationProblem, EvacuationSimulationResult> {

    private final List<Action> initializationActions;

    public EvacuationInitializationCompleteEvent(Algorithm<EvacuationSimulationProblem, EvacuationSimulationResult> algorithm, List<Action> initializationActions) {
        super(algorithm, 0);
        this.initializationActions = initializationActions;
    }

    public List<Action> getInitializationActions() {
        return Collections.unmodifiableList(initializationActions);
    }

}
