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
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.algorithm.state.EvacuationStateControllerInterface;
import org.zet.cellularautomaton.algorithm.state.PropertyAccess;
import org.zet.cellularautomaton.algorithm.state.PropertyUpdate;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class SwapAction extends Action {
    public static SwapAction NO_MOVE;

    /** The cell from where individual 1 moves. */
    protected EvacCellInterface cell1;
    /** The cell from where individual 2 moves. */
    protected EvacCellInterface cell2;
    /** The (exact) time, when individual 1 will arrive at cell 2. */
    protected double arrivalTime1;
    /** The (exact) time, when individual 2 will arrive at cell 1. */
    protected double arrivalTime2;
    /** The (exact) time, when individual 1 starts moving to cell 2. */
    protected double startTime1;
    /** The (exact) time, when individual 2 starts moving to cell 1. */
    protected double startTime2;
    /** The number of individual 1 that is moved. */
    private int individualNumber1;
    /** The number of individual 2 that is moved. */
    private int individualNumber2;
    private PropertyUpdate cell1Update;
    private PropertyUpdate cell2Update;

    /**
     * Creates a new instance of a move action. This action starts at the cell from where the individual leaves and ends
     * at the final point of the individual's movement. The action is performed by the individual standing on the start
     * cell.
     *
     * @param cell1 The cell from where one individual starts to move
     * @param cell2 The cell from where the other individual starts to move
     */
    public SwapAction(EvacCellInterface cell1, EvacCellInterface cell2, PropertyAccess es) {
        this(cell1, cell2, es.propertyFor(cell1.getState().getIndividual()).getStepEndTime(), es.propertyFor(cell1.getState().getIndividual()).getStepStartTime(),
                cell1.getState().getIndividual().getNumber(),
                es.propertyFor(cell2.getState().getIndividual()).getStepEndTime(),
                es.propertyFor(cell2.getState().getIndividual()).getStepStartTime(),
                cell2.getState().getIndividual().getNumber());
        if (cell1.getState().isEmpty()) {
            throw new IllegalArgumentException("The starting cell must not be empty!");
        }
        if (cell2.getState().isEmpty()) {
            throw new IllegalArgumentException("The starting cell must not be empty!");
        }
    }

    private SwapAction(
            EvacCellInterface cell1,
            EvacCellInterface cell2,
            double getStepEndTime1,
            double getStepStartTime1,
            int getNumber1,
            double getStepEndTime2,
            double getStepStartTime2,
            int getNumber2) {

        this.cell1 = cell1;
        this.cell2 = cell2;
        this.arrivalTime1 = getStepEndTime1;
        this.startTime1 = getStepStartTime1;
        this.individualNumber1 = getNumber1;
        this.arrivalTime2 = getStepEndTime2;
        this.startTime2 = getStepStartTime2;
        this.individualNumber2 = getNumber2;
    }

    public SwapAction(EvacCellInterface cell1, EvacCellInterface cell2, PropertyUpdate c1update, PropertyUpdate c2update) {
        this(cell1, cell2, c1update.getStepEndTime().get(), c1update.getStepStartTime().get(), cell1.getState().getIndividual().getNumber(),
                c2update.getStepEndTime().get(), c2update.getStepStartTime().get(), cell2.getState().getIndividual().getNumber());
        this.cell1Update = c1update;
        this.cell2Update = c2update;
    }

    public EvacCellInterface cell1() {
        return cell1;
    }

    public EvacCellInterface cell2() {
        return cell2;
    }

    public int getIndividualNumber1() {
        return individualNumber1;
    }

    public int getIndividualNumber2() {
        return individualNumber2;
    }

    public double arrivalTime1() {
        return arrivalTime1;
    }

    public double arrivalTime2() {
        return arrivalTime2;
    }

    public double startTime1() {
        return startTime1;
    }

    public double startTime2() {
        return startTime2;
    }

    public PropertyUpdate getCell1Update() {
        return cell1Update;
    }

    public PropertyUpdate getCell2Update() {
        return cell2Update;
    }

    @Override
    public void execute(EvacuationState es, EvacuationStateControllerInterface ec) throws InconsistentPlaybackStateException {
        if (cell1.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(-1, this, "There is no Individual on cell 1.");
        }
        if (cell2.getState().isEmpty()) {
            throw new InconsistentPlaybackStateException(-1, this, "There is no Individual on cell 2.");
        }

        //cell1.getRoom().swapIndividuals( cell1, cell2 );
        ec.swap(cell1, cell2);
    }

    @Override
    public void executeDelayed(EvacuationState es, EvacuationStateControllerInterface ec) {
    }

    @Override
    public String toString() {
        String representation = "";

        representation += cell1.getState().getIndividual() + " moves from ";
        representation += cell1 + " to ";
        representation += cell2 + ". \n";
        representation += cell2.getState().getIndividual() + " moves from ";
        representation += cell2 + " to ";
        representation += cell1 + ". \n";

        return representation;
    }

}
