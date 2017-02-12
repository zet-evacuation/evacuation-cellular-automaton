/* zet evacuation tool copyright (c) 2007-15 zet evacuation team
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
package org.zet.cellularautomaton.results;

import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;

/**
 * Represents the fact that an individual moves from one cell to another.
 * @author Daniel R. Schmidt
 *
 */
public class MoveAction extends Action {

    public static final MoveAction NO_MOVE = new MoveAction(null, null, 0, 0, 0);

    /** The cell from where the individual moves */
    protected EvacCellInterface from;
    /** The cell to where the individual moves */
    protected EvacCellInterface to;
    /** The (exact) time, when the individual will arrive at the 
     *  target cell
     */
    protected double arrivalTime;
    /** The (exact) time, when the individual starts moving to the
     *  target cell
     */
    protected double startTime;
    /** The number of the individual that is moved */
    private int individualNumber;

    /**
     * Creates a new instance of a move action. This action starts at the cell from where the individual leaves and ends
     * at the final point of the individual's movement. The action is performed by the individual standing on the start
     * cell.
     *
     * @param from The cell from where the individual starts to move
     * @param to The cell where the individual arrives
     * @param individual the individual that is moved
     * @param arrivalTime
     * @param startTime
     */
    public MoveAction(EvacCellInterface from, EvacCellInterface to, double arrivalTime, double startTime) {
        this(from, to, arrivalTime, startTime, from.getState().getIndividual().getNumber());
        if (from.getState().isEmpty()) {
            throw new IllegalArgumentException("The starting cell must not be empty!");
        }

        if (!to.getState().isEmpty() && !(to == from)) {
            throw new IllegalArgumentException("The taget cell is not empty!");
        }
    }

    protected MoveAction(EvacCellInterface from, EvacCellInterface to, double arrivalTime, double startTime, int individualNumber) {
        this.from = from;
        this.to = to;
        this.arrivalTime = arrivalTime;
        this.startTime = startTime;
        this.individualNumber = individualNumber;
    }

    public EvacCellInterface from() {
        return from;
    }

    public int getIndividualNumber() {
        return individualNumber;
    }

    public EvacCellInterface to() {
        return to;
    }

    public double arrivalTime() {
        return arrivalTime;
    }

    public double startTime() {
        return startTime;
    }

    @Override
    public void execute(EvacuationCellularAutomaton onCA, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        if (from.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(
                    -1,
                    this,
                    "Cannot move individual because it is not there.");
        }

        if (!(to == from) && !to.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(
                    -1,
                    this,
                    "Cannot move individual because there is an individual on the target cell.");
        }
        ec.move(from, to);        
    }

    @Override
    public void executeDelayed(EvacuationState es) {
    }

    @Override
    public String toString() {
        String representation = "";

        representation += from.getState().getIndividual() + " moves from ";
        representation += from + " to ";
        representation += to + ".";

        return representation;
    }

    @Override
    public Action adoptToCA(EvacuationCellularAutomaton targetCA) throws CADoesNotMatchException {
        EvacCellInterface newFrom = adoptCell(from, targetCA);
        if (newFrom == null) {
            throw new CADoesNotMatchException(
                    this,
                    "Could not find the starting cell " + from + " in the new CA.");
        }

        EvacCellInterface newTo = adoptCell(to, targetCA);
        if (to == null) {
            throw new CADoesNotMatchException(
                    this,
                    "Could not find the ending cell " + to + " in the new CA.");
        }

        return new MoveAction(newFrom, newTo, this.arrivalTime, this.startTime, this.individualNumber);
    }

    public EvacCellInterface getFrom() {
        return from;
    }

    public EvacCellInterface getTo() {
        return to;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getStartTime() {
        return startTime;
    }
}
