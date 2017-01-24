package org.zet.cellularautomaton.algorithm.rule;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.results.IndividualStateChangeAction;

/**
 * A movement rule that supports movement at non integral points in time.
 *
 * @author Jan-Philipp Kappmeier
 */
public abstract class SmoothMovementRule extends AbstractMovementRule {

    protected Individual individual;
    
    /**
     * Decides whether the rule can be applied to the current cell. Returns {@code true} if the cell is occupied by an
     * individual or {@code false} otherwise. Individuals standing on an exit cell do not move any more. This is
     * necessary, as the rule can take out individuals out of the simulation only, if their last step is finished. To
     * avoid problems of individuals moving forever, the movement rule should only be applied if an individual is not
     * already standing on an evacuation cell.
     *
     * @param cell the cell
     * @return true if the rule can be executed
     */
    @Override
    public boolean executableOn(EvacCellInterface cell) {
        return !(cell instanceof ExitCell) && !cell.getState().isEmpty();
    }

    @Override
    protected void onExecute(EvacCellInterface cell) {
        individual = cell.getState().getIndividual();
        if (es.propertyFor(individual).isAlarmed()) {
            if (canMove(individual)) {
                if (isIndividualMoving()) {
                    if (isDirectExecute()) { // we are in a "normal" simulation
                        EvacCellInterface targetCell = selectTargetCell(cell, computePossibleTargets(cell, true));
                        setMoveRuleCompleted(true);
                        move(cell, targetCell);
                    } else { // only calculate possible movements, used for swap cellular automaton
                        computePossibleTargets(cell, false);
                        setMoveRuleCompleted(true);
                    }                    
                } else {
                    setMoveRuleCompleted(true);
                    noMove(cell);
                }
            } else { // Individual can't move, it is already moving
                setMoveRuleCompleted(false); // TODO why is here false?
            }
        } else { // Individual is not alarmed, that means it remains standing on the cell
            setMoveRuleCompleted(true);
            noMove(cell);
        }

        recordAction(new IndividualStateChangeAction(individual, es));
    }
    
    /**
     * Decides whether the individual actually moves if it is allowed to.
     * @return 
     */
    boolean isIndividualMoving() {
       return true; 
    }
    
    abstract void noMove(EvacCellInterface cell);

}
