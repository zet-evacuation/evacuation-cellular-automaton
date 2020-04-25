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
import java.util.Collection;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.ExitCell;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.potential.Potential;
import org.zet.cellularautomaton.results.VoidAction;

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

    private double getResponse(EvacCellInterface cell, Exit exit) {
        final Potential pot = es.getCellularAutomaton().getPotentialFor(exit);
        // Constants
        Individual ind = cell.getState().getIndividual();
        double speed = es.propertyFor(ind).getRelativeSpeed();

        // Exit dependant values                                    
        double distance = Double.MAX_VALUE;
        if (pot.getPotential(cell) > 0) {
            distance = pot.getPotentialDouble(cell);
        }
        double movingTime = distance / speed;
        Collection<ExitCell> exitCells = exit.getExitCluster();
        int exitCapacity = exitCells.size();

        // calculate number of individuals that are heading to the same exit and closer to it            
        ArrayList<Individual> otherInds = new ArrayList<>();
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.addAll(es.getCellularAutomaton().getRooms());
        for (Room room : rooms) {
            for (Individual i : room.getIndividuals()) {
                otherInds.add(i);
            }
        }
        int queueLength = 0;
        if (otherInds != null) {
            for (Individual otherInd : otherInds) {
                if (!otherInd.equals(ind)) {
                    if (es.propertyFor(otherInd).getStaticPotential() == pot) {
                        if (es.propertyFor(otherInd).getStaticPotential().getPotentialDouble(es.propertyFor(otherInd).getCell()) > 0) {
                            if (es.propertyFor(otherInd).getStaticPotential().getPotentialDouble(es.propertyFor(otherInd).getCell()) < distance) {
                                queueLength++;
                            }
                        }
                    }
                }
            }
        }

        int wrongDirectedNeighbours = 0;
        for (EvacCellInterface neighbour : cell.getDirectNeighbors()) {
            if (!neighbour.getState().isEmpty()) {
                if (es.propertyFor(neighbour.getState().getIndividual()).getStaticPotential() != pot) {
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
     * @return 
     */
    @Override
    protected VoidAction onExecute(EvacCellInterface cell) {

        //ArrayList<Potential> exits = new ArrayList<>();
        //exits.addAll(es.getCellularAutomaton().getExits());
        Potential newPot = es.propertyFor(cell.getState().getIndividual()).getStaticPotential();
        double response = Double.MAX_VALUE;
        for (Exit exit : es.getCellularAutomaton().getExits()) {
            Potential pot = es.getCellularAutomaton().getPotentialFor(exit);
            final double newResponse = getResponse(cell, exit);
            if ( newResponse< response) {
                response = newResponse;
                newPot = pot;
            }
        }
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(newPot);
        return VoidAction.VOID_ACTION;
    }
}
