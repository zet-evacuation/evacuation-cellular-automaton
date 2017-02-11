package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.results.MoveAction;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportRule extends AbstractMoveRule {

    @Override
    public boolean executableOn(EvacCellInterface cell) {
        if (cell instanceof TeleportCell && super.executableOn(cell)) {
            return canMove(cell.getState().getIndividual());
        }
        return false;
    }

    @Override
    protected MoveAction onExecute(EvacCellInterface cell) {
        final TeleportCell tc = (TeleportCell) cell;
        if (tc.targetCount() > 0) {
            return performTeleport(tc);
        }
        return MoveAction.NO_MOVE;
    }

    private MoveAction performTeleport(TeleportCell teleportCell) {
        if (teleportCell.getTarget(0).getState().getIndividual() == null && teleportCell.getTarget(0).getUsedInTimeStep() < es.getTimeStep()) {
            teleportCell.setTeleportFailed(false);
            teleportCell.getTarget(0).setUsedInTimeStep(es.getTimeStep());
            return move(teleportCell, teleportCell.getTarget(0));
        } else {
            teleportCell.setTeleportFailed(true);
            return MoveAction.NO_MOVE;
        }
    }

    @Override
    public MoveAction move(EvacCellInterface teleportCell, EvacCellInterface target) {
        double targetFreeAt = target.getOccupiedUntil();
        double moveTime = Math.max(targetFreeAt, es.propertyFor(teleportCell.getState().getIndividual()).getStepEndTime());
        return new MoveAction(teleportCell, target, moveTime, moveTime);
    }
}
