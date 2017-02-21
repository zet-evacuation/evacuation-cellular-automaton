package org.zet.cellularautomaton.results;

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
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    @Override
    public String toString() {
        return "VoidAction";
    }
    
}
