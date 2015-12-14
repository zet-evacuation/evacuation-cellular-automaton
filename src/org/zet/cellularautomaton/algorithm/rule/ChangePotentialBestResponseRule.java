/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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

import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.potential.StaticPotential;
import org.zet.cellularautomaton.Room;

/**
 *
 * @author Joscha Kulbatzki
 */
public class ChangePotentialBestResponseRule extends AbstractPotentialChangeRule {

    private static final double QUEUEING_TIME_WEIGHT_FACTOR = 0.5;
    private static final double MOVING_TIME_WEIGHT_FACTOR = 0.5;
    private static final double NEIGHBOUR_WEIGHT_FACTOR = 0;

    @Override
    protected boolean wantsToChange(Individual i) {
        return true;
    }

    private double getResponse(EvacCell cell, StaticPotential pot) {
        // Constants
        Individual ind = cell.getState().getIndividual();
        double speed = ind.getRelativeSpeed();

        // Exit dependant values                                    
        double distance = Double.MAX_VALUE;
        if (pot.getPotential(cell) > 0) {
            distance = pot.getDistance(cell);
        }
        double movingTime = distance / speed;
        List<ExitCell> exitCells = pot.getAssociatedExitCells();
        int exitCapacity = exitCells.size();

        // calculate number of individuals that are heading to the same exit and closer to it            
        ArrayList<Individual> otherInds = new ArrayList<>();
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.addAll(esp.getCellularAutomaton().getRooms());
        for (Room room : rooms) {
            for (Individual i : room.getIndividuals()) {
                otherInds.add(i);
            }
        }
        int queueLength = 0;
        if (otherInds != null) {
            for (Individual otherInd : otherInds) {
                if (!otherInd.equals(ind)) {
                    if (otherInd.getStaticPotential() == pot) {
                        if (otherInd.getStaticPotential().getDistance(otherInd.getCell()) > 0) {
                            if (otherInd.getStaticPotential().getDistance(otherInd.getCell()) < distance) {
                                queueLength++;
                            }
                        }
                    }
                }
            }
        }

        int wrongDirectedNeighbours = 0;
        for (EvacCell neighbour : cell.getDirectNeighbors()) {
            if (!neighbour.getState().isEmpty()) {
                if (neighbour.getState().getIndividual().getStaticPotential() != pot) {
                    wrongDirectedNeighbours++;
                }
            }
        }

            //System.out.println("Potential = " + pot.getID());
        //System.out.println("Queue / Kapa = " + queueLength + " / " + exitCapacity + " = " + (queueLength / exitCapacity));
        //System.out.println("Dist / Speed = " + distance + " / " + speed + " = " + (distance / speed));
        // calculateEstimatedEvacuationTime
        return responseFunction1(queueLength, exitCapacity, movingTime);
            //return responseFunction2(queueLength,exitCapacity,movingTime,wrongDirectedNeighbours);

    }

    private double responseFunction1(int queueLength, int exitCapacity, double movingTime) {
        return (QUEUEING_TIME_WEIGHT_FACTOR * (queueLength / exitCapacity)) + (MOVING_TIME_WEIGHT_FACTOR * movingTime);
    }

    private double responseFunction2(int queueLength, int exitCapacity, double movingTime, int wrongDirectedNeighbours) {
        return (QUEUEING_TIME_WEIGHT_FACTOR * (queueLength / exitCapacity)) + (MOVING_TIME_WEIGHT_FACTOR * movingTime) + (NEIGHBOUR_WEIGHT_FACTOR * wrongDirectedNeighbours);
    }

    /**
     *
     * @param cell
     */
    @Override
    protected void onExecute(EvacCell cell) {

        ArrayList<StaticPotential> exits = new ArrayList<>();
        exits.addAll(esp.getCellularAutomaton().getStaticPotentials());
        StaticPotential newPot = cell.getState().getIndividual().getStaticPotential();
        double response = Double.MAX_VALUE;
        for (StaticPotential pot : exits) {
            if (getResponse(cell, pot) < response) {
                response = getResponse(cell, pot);
                newPot = pot;
            }
        }
        cell.getState().getIndividual().setStaticPotential(newPot);

    }
}
