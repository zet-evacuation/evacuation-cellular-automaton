package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.TeleportCell;

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
    protected void onExecute(EvacCellInterface cell) {
        final TeleportCell tc = (TeleportCell) cell;
        if (tc.targetCount() > 0) {
            performTeleport(tc);
        }
    }

    private void performTeleport(TeleportCell tc) {
        double targetFreeAt = tc.getTarget(0).getOccupiedUntil();

        if (tc.getTarget(0).getState().getIndividual() == null && tc.getTarget(0).getUsedInTimeStep() < es.getTimeStep()) {
            double moveTime = Math.max(targetFreeAt, es.propertyFor(tc.getState().getIndividual()).getStepEndTime());
            es.propertyFor(tc.getState().getIndividual()).setStepStartTime(moveTime);
            es.propertyFor(tc.getState().getIndividual()).setStepEndTime(moveTime);

            move(tc, tc.getTarget(0));
            tc.setTeleportFailed(false);
            tc.getTarget(0).setUsedInTimeStep(es.getTimeStep());
        } else {
            tc.setTeleportFailed(true);
        }
    }

    @Override
    public void move(EvacCellInterface from, EvacCellInterface target) {
        ec.move(from, target);
    }

    /**
     * Decides randomly if an individual moves. (falsch)
     *
     * @param i An individual with a given parameterSet
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
}
