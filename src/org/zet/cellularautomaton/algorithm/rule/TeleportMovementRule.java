package org.zet.cellularautomaton.algorithm.rule;

import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.EvacCellInterface;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportMovementRule extends WaitingMovementRule {

    @Override
    public boolean executableOn(EvacCellInterface cell) {
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
    protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
        List<EvacCellInterface> targets = super.computePossibleTargets(fromCell, onlyFreeNeighbours);

        ArrayList<EvacCellInterface> returned = new ArrayList<>(); // create new list to avoid concurrent modification
        double time = es.getTimeStep();
        for (EvacCellInterface cell : targets) {
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
