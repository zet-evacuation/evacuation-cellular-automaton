package org.zet.cellularautomaton.results;

import java.util.Map;
import org.zet.cellularautomaton.EvacCellInterface;
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
    void adoptToCA(Map<EvacCellInterface, EvacCellInterface> selfMap) throws CADoesNotMatchException {
    }

    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
    }

    @Override
    public void executeDelayed(EvacuationState es) {
    }

    @Override
    public String toString() {
        return "VoidAction";
    }
    
}
