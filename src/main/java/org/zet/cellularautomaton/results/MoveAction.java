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
package org.zet.cellularautomaton.results;

import java.util.Objects;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.PropertyUpdate;

/**
 * Represents the fact that an individual moves from one cell to another.
 * @author Daniel R. Schmidt
 *
 */
public class MoveAction extends Action {

    public static final MoveAction NO_MOVE = new MoveAction(null, null, 0, 0, 0);

    /** The cell from where the individual moves. */
    protected EvacCellInterface from;
    /** The cell to where the individual moves. */
    protected EvacCellInterface to;
    /** The (exact) time, when the individual will arrive at the target cell. */
    protected double arrivalTime;
    /** The (exact) time, when the individual starts moving to the target cell. */
    protected double startTime;
    /** The number of the individual that is moved. */
    private int individualNumber;
    private Individual individual;
    private PropertyUpdate update;

    /**
     * Creates a new instance of a move action. This action starts at the cell from where the individual leaves and ends
     * at the final point of the individual's movement. The action is performed by the individual standing on the start
     * cell.
     *
     * @param from The cell from where the individual starts to move
     * @param to The cell where the individual arrives
     * @param update
     */
    public MoveAction(EvacCellInterface from, EvacCellInterface to, PropertyUpdate update) {
        this(from, to, update.getStepEndTime().get(), update.getStepStartTime().get(), from.getState().getIndividual().getNumber());
        if (from.getState().isEmpty()) {
            throw new IllegalArgumentException("The starting cell must not be empty!");
        }

        if (!to.getState().isEmpty() && !(to == from)) {
            throw new IllegalArgumentException("The taget cell is not empty!");
        }
        this.individual = Objects.requireNonNull(from.getState().getIndividual());
        this.update = update;
    }

    /**
     * Creates a new instance of a move action. This action starts at the cell from where the individual leaves and ends
     * at the final point of the individual's movement. The action is performed by the individual standing on the start
     * cell.
     *
     * @param from The cell from where the individual starts to move
     * @param to The cell where the individual arrives
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
        this.individual = Objects.requireNonNull(from.getState().getIndividual());
        this.update = PropertyUpdate.forMove(startTime, arrivalTime).createUpdate();
    }
    
    public MoveAction(EvacCellInterface from, EvacCellInterface to, double arrivalTime, double startTime, PropertyUpdate update) {
        this(from, to, arrivalTime, startTime, from.getState().getIndividual().getNumber());
        if (from.getState().isEmpty()) {
            throw new IllegalArgumentException("The starting cell must not be empty!");
        }

        if (!to.getState().isEmpty() && !(to == from)) {
            throw new IllegalArgumentException("The taget cell is not empty!");
        }
        this.individual = Objects.requireNonNull(from.getState().getIndividual());
        this.update = PropertyUpdate.extend(update).withStepStartTime(startTime).withStepEndTime(arrivalTime).createUpdate();
    }

    protected MoveAction(EvacCellInterface from, EvacCellInterface to, double arrivalTime, double startTime, int individualNumber) {
        this.from = from;
        this.to = to;
        this.arrivalTime = arrivalTime;
        this.startTime = startTime;
        this.individualNumber = individualNumber;
        if (from != null) {
            this.individual = Objects.requireNonNull(from.getState().getIndividual());
        }
    }

    public MoveAction(MoveAction a, PropertyUpdate update) {
        this.from = a.from;
        this.to = a.to;
        this.arrivalTime = a.arrivalTime;
        this.startTime = a.startTime;
        this.individual = a.individual;
        this.individualNumber = a.individualNumber;
        if (!update.getStepEndTime().isPresent() || update.getStepEndTime().get() != arrivalTime
                || !update.getStepStartTime().isPresent() || update.getStepStartTime().get() != startTime) {
            this.update = PropertyUpdate.extend(update).withStepStartTime(startTime).withStepEndTime(arrivalTime).createUpdate();
        } else {
            this.update = update;
        }
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
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        EvacCellInterface newFrom = adoptCell(from);
        EvacCellInterface newTo = adoptCell(to);

        if (newFrom.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(
                    -1,
                    this,
                    "Cannot move individual because it is not there.");
        }

        if (!(newTo == newFrom) && !newTo.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(
                    -1,
                    this,
                    "Cannot move individual because there is an individual on the target cell.");
        }
        update.apply(es.propertyFor(individual));

        ec.increaseDynamicPotential(newTo);

        ec.move(newFrom, newTo);
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    @Override
    public String toString() {
        String representation = "";

        EvacCellInterface newFrom = adoptCell(from);
        EvacCellInterface newTo = adoptCell(to);
        
        representation += individual + " moves from ";
        representation += newFrom + " to ";
        representation += newTo + ".";

        return representation;
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

    public PropertyUpdate getPropertyUpdate() {
        return update;
    }
}
