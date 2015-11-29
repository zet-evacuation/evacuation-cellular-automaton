package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.TeleportCell;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRule extends WaitingMovementRule {

    @Override
    public boolean executableOn(EvacCell cell) {
        return cell instanceof TeleportCell ? super.executableOn(cell) && !((TeleportCell) cell).isTeleportFailed() : super.executableOn(cell);
    }

    /**
     * Selects the possible targets including the current cell.
     *
     * @param fromCell the current sell
     * @param onlyFreeNeighbours indicates whether only free neighbours or all neighbours are included
     * @return a list containing all neighbours and the from cell
     */
    @Override
    protected List<EvacCell> computePossibleTargets(EvacCell fromCell, boolean onlyFreeNeighbours) {
        List<EvacCell> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);

        ArrayList<EvacCell> returned = new ArrayList<>(); // create new list to avoid concurrent modification
        double time = esp.getCellularAutomaton().getTimeStep();
        for (EvacCell cell : targets) {
            if (!cell.isOccupied(time)) {
                returned.add(cell);
            }
        }
        if (!returned.contains(fromCell)) {
            returned.add(fromCell);
        }

        return returned;
    }
}
