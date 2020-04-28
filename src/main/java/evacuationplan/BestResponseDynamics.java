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
package evacuationplan;

import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.EvacCellInterface;
import org.zet.cellularautomaton.algorithm.state.EvacuationState;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Exit;
import org.zet.cellularautomaton.potential.Potential;

/**
 * The class {@code BestResponseDynamics} ...
 *
 * @author Joscha Kulbatzki
 * @author Jan-Philipp Kappmeier
 */
public class BestResponseDynamics {

    private static final double QUEUEING_TIME_WEIGHT_FACTOR = 0.5;
    private static final double MOVING_TIME_WEIGHT_FACTOR = 0.5;
    private EvacuationState es;

    /**
     * Creates a new instance of {@code BestResponseDynamics}.
     */
    public BestResponseDynamics() {

    }

    public void computeAssignmentBasedOnBestResponseDynamics(EvacuationCellularAutomaton ca, List<Individual> individuals) {
        int c = 0;
        while (true) {
            c++;
            int swapped = 0;
            for (Individual i : individuals) {
                swapped += computePotential(es.propertyFor(i).getCell(), ca);
            }
            System.out.println("Swapped in iteration " + c + ": " + swapped);
            if (swapped == 0) {
                break;
            }
        }
        System.out.println("Best Response Rounds: " + c);
    }

    public int computePotential(EvacCellInterface cell, EvacuationCellularAutomaton ca) {
        ArrayList<Potential> exits = new ArrayList<>();
        //exits.addAll(ca.getExits());
        Potential newPot = es.propertyFor(cell.getState().getIndividual()).getStaticPotential();
        double response = Double.MAX_VALUE;
        for (Exit exit : es.getCellularAutomaton().getExits()) {
            double newResponse = getResponse(ca, cell, exit);
            if (newResponse < response) {
                response = newResponse;
                newPot = es.getCellularAutomaton().getPotentialFor(exit);
            }
        }

        Potential oldPot = es.propertyFor(cell.getState().getIndividual()).getStaticPotential();
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(newPot);
        if (!oldPot.equals(newPot)) {
            return 1;
        } else {
            return 0;
        }
    }

    private double getResponse(EvacuationCellularAutomaton ca, EvacCellInterface cell, Exit exit) {
        Potential pot = es.getCellularAutomaton().getPotentialFor(exit);

        // Constants
        Individual ind = cell.getState().getIndividual();
        double speed = es.propertyFor(ind).getRelativeSpeed();

        // Exit dependant values
        double distance = Double.MAX_VALUE;
        if (pot.getPotentialDouble(cell) >= 0) {
            distance = pot.getPotentialDouble(cell);
        }
        double movingTime = distance / speed;

        double exitCapacity = exit.getCapacity(); //ca.getExitToCapacityMapping().get(pot);
        //System.out.println("Exit: " + pot.getID() + " : " + exitCapacity);

        // calculate number of individuals that are heading to the same exit and closer to it
        ArrayList<Individual> otherInds = new ArrayList<>();
        //cell.getRoom().getIndividuals();
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.addAll(ca.getRooms());
        for (Room room : rooms) {
            for (Individual i : room.getIndividuals()) {
                otherInds.add(i);
            }
        }

        int queueLength = 0;
        for (Individual otherInd : otherInds) {
            if (!otherInd.equals(ind)) {
                if (es.propertyFor(otherInd).getStaticPotential() == pot) {
                    if (es.propertyFor(otherInd).getStaticPotential().getPotentialDouble(es.propertyFor(otherInd).getCell()) >= 0) {
                        if (es.propertyFor(otherInd).getStaticPotential().getPotentialDouble(es.propertyFor(otherInd).getCell()) < distance) {
                            queueLength++;
                        }
                    }
                }
            }
        }
        //System.out.println("Potential = " + pot.getID());
        //System.out.println("Queue / Kapa = " + queueLength + " / " + exitCapacity + " = " + (queueLength / exitCapacity));
        //System.out.println("Dist / Speed = " + distance + " / " + speed + " = " + (distance / speed));

        // calculateEstimatedEvacuationTime
        return responseFunction1(queueLength, exitCapacity, movingTime);

    }

    private double responseFunction1(int queueLength, double exitCapacity, double movingTime) {
        return (QUEUEING_TIME_WEIGHT_FACTOR * (queueLength / exitCapacity)) + (MOVING_TIME_WEIGHT_FACTOR * movingTime);
    }

    /**
     * Returns the name of the class.
     *
     * @return the name of the class
     */
    @Override
    public String toString() {
        return "BestResponseDynamics";
    }
}
