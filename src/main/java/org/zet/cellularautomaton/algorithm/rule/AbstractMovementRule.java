/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.zet.cellularautomaton.algorithm.rule;

import org.zetool.common.util.Direction8;
import org.zet.cellularautomaton.DoorCell;
import org.zet.cellularautomaton.Individual;
import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zetool.common.debug.Debug;

/**
 * @author Jan-Philipp Kappmeier
 *
 */
public abstract class AbstractMovementRule extends AbstractMoveRule implements MovementRule {

    protected double speed;
    protected double dist;

    private boolean directExecute;
    private boolean moveCompleted;
    private List<EvacCellInterface> possibleTargets;

    public AbstractMovementRule() {
        directExecute = true;
        moveCompleted = false;
    }

    /**
     * Computes and returns possible targets and also sets them, such that they can be retrieved using
     * {@link #getPossibleTargets()}.
     *
     * @param fromCell
     * @param onlyFreeNeighbours
     * @return
     */
    protected List<EvacCellInterface> computePossibleTargets(EvacCellInterface fromCell, boolean onlyFreeNeighbours) {
        possibleTargets = new ArrayList<>();
        List<EvacCellInterface> neighbors = onlyFreeNeighbours ? fromCell.getFreeNeighbours() : fromCell.getNeighbours();

        Direction8 dir = es.propertyFor(fromCell.getState().getIndividual()).getDirection();

        for (EvacCellInterface evacCell : neighbors) {
            if (es.propertyFor(fromCell.getState().getIndividual()).isSafe() && !evacCell.isSafe()) {
                continue; // ignore all moves that would mean walking out of safe areas
            }
            if (fromCell instanceof DoorCell && evacCell instanceof DoorCell) {
                possibleTargets.add(evacCell);
                continue;
            }
            Direction8 rel = fromCell.getRelative(evacCell);
            if (dir == rel) {
                possibleTargets.add(evacCell);
            } else if (dir == rel.getClockwise()) {
                possibleTargets.add(evacCell);
            } else if (dir == rel.getClockwise().getClockwise()) {
                possibleTargets.add(evacCell);
            } else if (dir == rel.getCounterClockwise()) {
                possibleTargets.add(evacCell);
            } else if (dir == rel.getCounterClockwise().getCounterClockwise()) {
                possibleTargets.add(evacCell);
            }
        }
        return possibleTargets;
    }

    /**
     * Returns the possible targets already sorted by priority. The possible targets either have been set before using {@link #setPossibleTargets(java.util.ArrayList)
     * }
     * ore been computed using {@link #getPossibleTargets(ds.ca.evac.EvacCell, boolean) }.
     *
     * @return a list of possible targets.
     */
    @Override
    public List<EvacCellInterface> getPossibleTargets() {
        return possibleTargets;
    }

    /**
     * In this simple implementation always the first possible cell is returned. As this method should be overridden, a
     * warning is printed to the err log if it is used.
     *
     * @param cell not used in the simple imlementation
     * @param targets possible targets (only the first one is used)
     * @return the first cell of the possible targets
     */
    @Override
    public EvacCellInterface selectTargetCell(EvacCellInterface cell, List<EvacCellInterface> targets) {
        Debug.globalLogger.warning("Not-overriden target cell selection is used.");
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("Target list cannot be empty.");
        }
        return targets.get(0);
    }

    /**
     * Returns a sway delay if the individual is changing the walking direction.
     *
     * @param ind
     * @param direction
     * @return
     */
    protected double getSwayDelay(Individual ind, Direction8 direction) {
        if (es.propertyFor(ind).getDirection() == direction) {
            return 0;
        } else if (es.propertyFor(ind).getDirection() == direction.getClockwise()
                || es.propertyFor(ind).getDirection() == direction.getCounterClockwise()) {
            return 0.5;
        } else if (es.propertyFor(ind).getDirection() == direction.getClockwise().getClockwise()
                || es.propertyFor(ind).getDirection() == direction.getCounterClockwise().getCounterClockwise()) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Sets the time when the current movement is over for an individual and updates the needed time in the cellular
     * automaton. Fractional values are accepted and are rounded up to the next integral value, to be used in the
     * integral cellular automaton. Updates also the step end time for the given individual (the time is not rounded).
     *
     * @param i the individual
     * @param d the (real) time when the movement is over
     */
    protected void setStepEndTime(Individual i, double d) {
        es.propertyFor(i).setStepEndTime(d);
    }

    @Override
    public boolean isDirectExecute() {
        return directExecute;
    }

    @Override
    public void setDirectExecute(boolean directExecute) {
        this.directExecute = directExecute;
    }

    @Override
    public boolean isMoveCompleted() {
        return moveCompleted;
    }

    protected void setMoveRuleCompleted(boolean moveCompleted) {
        this.moveCompleted = moveCompleted;
    }

}
