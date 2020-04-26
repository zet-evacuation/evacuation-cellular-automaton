package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.IndividualStateChangeAction;
import org.zet.cellularautomaton.results.MoveAction;

/**
 * A movement rule that supports movement at non integral points in time. Generally, movement is
 * divided into several situations.
 * <ul>
 * <li>Inactive: an inactive {@link Individual}, for example at the beginning.</li>
 * <li>Currently moving: when the {@link Individual} is still moving from earlier moves in this step</li>
 * <li>Skip: when the {@link Individual} skips a movement step for some reason, e.g. slacking</li>
 * <li>Moving: the {@link Individual} actually wants to move.</li>
 * </ul>
 *
 * The last step, {@literal Moving} is split into two cases, depending on whether the step should be
 * directly executed or not.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class SmoothMovementRule extends AbstractMovementRule {

    protected Individual individual;

    /**
     * Decides whether the rule can be applied to the current cell. Returns {@code true} if the cell
     * is occupied by an individual or {@code false} otherwise. Individuals standing on an exit cell
     * do not move any more. This is necessary, as the rule can take out individuals out of the
     * simulation only, if their last step is finished. To avoid problems of individuals moving
     * forever, the movement rule should only be applied if an individual is not already standing on
     * an evacuation cell.
     *
     * @param cell the cell on which the rule is executed
     * @return true if the rule can be executed
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !(cell instanceof ExitCell) && !cell.getState().isEmpty();
    }

    @Override
    protected MoveAction onExecute(EvacCellInterface cell) {
        individual = cell.getState().getIndividual();
        if (isActive()) {
            if (canMove(individual)) {
                if (wishToMove()) {
                    return performMove(cell);
                } else {
                    return skipStep(cell);
                }
            } else {
                // Individual can't move, it is already moving
                currentlyMoving(cell);
                return null;
            }
        } else {
            // Individual is not alarmed, that means it remains standing on the cell
            return remainInactive(cell);
        }
    }

    /**
     * Checks whether the {@link Individual} is alarmed, or not. The default implementation returns
     * always {@literal true}.
     *
     * @return {@literal true}
     */
    protected boolean isActive() {
        return true;
    }

    /**
     * Decides whether the individual actually moves if it is allowed to. If this is
     * {@literal false}, {@link #skipStep() } is called.
     *
     * @return
     */
    boolean wishToMove() {
        return true;
    }

    /**
     * Handles an inactive {@link Individual}.
     *
     * @param cell the cell on which the rule is executed
     */
    protected MoveAction remainInactive(EvacCellInterface cell) {
        setMoveRuleCompleted(true);
        return noMove(cell);
    }

    /**
     * Handles necessary updates when the {@link Individual} continues its last move.
     *
     * @param cell the cell on which the rule is executed
     */
    protected void currentlyMoving(EvacCellInterface cell) {
        setMoveRuleCompleted(false);
    }

    /**
     * Handles status updates when the {@link Individual} skips the step.
     *
     * @param cell the cell on which the rule is executed
     * @return 
     */
    protected MoveAction skipStep(EvacCellInterface cell) {
        setMoveRuleCompleted(true);
        return noMove(cell);
    }

    /**
     * Handles the {@link Individual}'s wish to move.
     *
     * @param cell the cell on which the rule is executed
     * @return 
     */
    protected MoveAction performMove(EvacCellInterface cell) {
        if (isDirectExecute()) { // we are in a "normal" simulation
            EvacCellInterface targetCell = selectTargetCell(cell, computePossibleTargets(cell, true));
            setMoveRuleCompleted(true);
            return move(cell, targetCell);
        } else { // only calculate possible movements, used for swap cellular automaton
            computePossibleTargets(cell, false);
            setMoveRuleCompleted(true);
        }
        return MoveAction.NO_MOVE;
    }

    abstract MoveAction noMove(EvacCellInterface cell);

}
