package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.MoveAction;

/**
 * The base rule for all rules indicating movement.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class AbstractMoveRule extends AbstractEvacuationRule<MoveAction> {

    /**
     * Decides, if an individual can move in individual step. This is possible, when the last move
     * was already finished at a time earlier than this time step.
     *
     * @param individual An individual with a given parameterSet
     * @return {@code true} if the individual moves or {@code false} otherwise.
     */
    protected boolean canMove(Individual individual) {
        return es.getTimeStep() >= es.propertyFor(individual).getStepEndTime();
    }

    public abstract MoveAction move(EvacCellInterface from, EvacCellInterface target);
}
