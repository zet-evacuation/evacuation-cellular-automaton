package org.zet.cellularautomaton.results;

import org.zet.cellularautomaton.EvacuationCellularAutomaton;

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
    public void execute(EvacuationCellularAutomaton onCA) throws InconsistentPlaybackStateException {
    }

    @Override
    public String toString() {
        return "VoidAction";
    }
    
}
