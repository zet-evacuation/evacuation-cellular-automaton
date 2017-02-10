package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.TeleportCell;
import org.zet.cellularautomaton.results.VoidAction;

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
    protected VoidAction onExecute(EvacCellInterface cell) {
        final TeleportCell tc = (TeleportCell) cell;
        if (tc.targetCount() > 0) {
            performTeleport(tc);
        }
        return VoidAction.VOID_ACTION;
    }

    private void performTeleport(TeleportCell teleportCell) {
        double targetFreeAt = teleportCell.getTarget(0).getOccupiedUntil();

        if (teleportCell.getTarget(0).getState().getIndividual() == null && teleportCell.getTarget(0).getUsedInTimeStep() < es.getTimeStep()) {
            double moveTime = Math.max(targetFreeAt, es.propertyFor(teleportCell.getState().getIndividual()).getStepEndTime());
            es.propertyFor(teleportCell.getState().getIndividual()).setStepStartTime(moveTime);
            es.propertyFor(teleportCell.getState().getIndividual()).setStepEndTime(moveTime);

            move(teleportCell, teleportCell.getTarget(0));
            teleportCell.setTeleportFailed(false);
            teleportCell.getTarget(0).setUsedInTimeStep(es.getTimeStep());
        } else {
            teleportCell.setTeleportFailed(true);
        }
    }

    @Override
    public void move(EvacCellInterface from, EvacCellInterface target) {
        ec.move(from, target);
    }
}
