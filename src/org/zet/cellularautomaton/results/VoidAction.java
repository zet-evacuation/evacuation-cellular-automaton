package org.zet.cellularautomaton.results;

import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class VoidAction extends Action {
    public static final VoidAction VOID_ACTION = new VoidAction();
    
    private VoidAction() {
    }

    @Override
    Action adoptToCA(EvacuationCellularAutomaton targetCA) throws CADoesNotMatchException {
        return this;
    }

    @Override
    public void execute(EvacuationCellularAutomaton onCA, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
    }

    @Override
    public void executeDelayed(EvacuationState es) {
    }

    @Override
    public String toString() {
        return "VoidAction";
    }
    
}
