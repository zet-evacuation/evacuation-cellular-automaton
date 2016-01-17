package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.TeleportCell;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class TeleportRule extends AbstractEvacuationRule {

    @Override
    public boolean executableOn(EvacCell cell) {
        boolean res = cell instanceof TeleportCell && super.executableOn(cell);
        if (res) {
            res = res && canMove(cell.getState().getIndividual());
        }
        return res;
    }

    static int counter = 0;

    @Override
    protected void onExecute(EvacCell cell) {
        final TeleportCell tc = (TeleportCell) cell;

        if (tc.targetCount() > 0 && tc.getTarget(0) != null) {

//						double beginTime = Math.max( i.getCell().getOccupiedUntil(), i.getStepEndTime() );
            double targetFreeAt = tc.getTarget(0).getOccupiedUntil();

            if (tc.getTarget(0).getState().getIndividual() == null && tc.getTarget(0).getUsedInTimeStep() < es.getTimeStep()) {
                double moveTime = Math.max(targetFreeAt, es.propertyFor(cell.getState().getIndividual()).getStepEndTime());
                //cell.getIndividual().setStepStartTime( cell.getIndividual().getStepEndTime() );
                es.propertyFor(cell.getState().getIndividual()).setStepStartTime(moveTime);
                es.moveIndividual(cell, tc.getTarget(0));
                tc.setTeleportFailed(false);
                counter++;
                //System.out.println( "Teleportiert: " + counter );
                if (es.getTimeStep() > tc.getTarget(0).getUsedInTimeStep()) {
                    tc.getTarget(0).setUsedInTimeStep(es.getTimeStep());
                }
            } else {
                tc.setTeleportFailed(true);
            }
        }
    }

    /**
     * Decides randomly if an individual moves. (falsch)
     *
     * @param i An individual with a given parameterSet
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
    //gibt true wieder, wenn geschwindigkeit von zelle und individuel (wkeit darueber) bewegung bedeuten
    protected boolean canMove(Individual i) {
        return es.getTimeStep() > es.propertyFor(i).getStepEndTime();
    }

}
