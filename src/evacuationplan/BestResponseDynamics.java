/*
 *
 * BestResponseDynamics.java
 * Created 04.06.2010, 23:58:56
 */
package evacuationplan;

import org.zet.cellularautomaton.EvacCell;
import org.zet.cellularautomaton.EvacuationCellularAutomaton;
import org.zet.cellularautomaton.Individual;
import org.zet.cellularautomaton.Room;
import org.zet.cellularautomaton.potential.StaticPotential;
import java.util.ArrayList;
import java.util.List;
import org.zet.cellularautomaton.EvacuationCellularAutomatonInterface;
import org.zet.cellularautomaton.algorithm.EvacuationState;

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

    public int computePotential(EvacCell cell, EvacuationCellularAutomatonInterface ca) {
        ArrayList<StaticPotential> exits = new ArrayList<>();
        exits.addAll(ca.getStaticPotentials());
        StaticPotential newPot = es.propertyFor(cell.getState().getIndividual()).getStaticPotential();
        double response = Double.MAX_VALUE;
        for (StaticPotential pot : exits) {
            if (getResponse(ca, cell, pot) < response) {
                response = getResponse(ca, cell, pot);
                newPot = pot;
            }
        }

        StaticPotential oldPot = es.propertyFor(cell.getState().getIndividual()).getStaticPotential();
        es.propertyFor(cell.getState().getIndividual()).setStaticPotential(newPot);
        if (!oldPot.equals(newPot)) {
            return 1;
        } else {
            return 0;
        }
    }

    private double getResponse(EvacuationCellularAutomatonInterface ca, EvacCell cell, StaticPotential pot) {

        // Constants
        Individual ind = cell.getState().getIndividual();
        double speed = es.propertyFor(ind).getRelativeSpeed();

        // Exit dependant values
        double distance = Double.MAX_VALUE;
        if (pot.getDistance(cell) >= 0) {
            distance = pot.getDistance(cell);
        }
        double movingTime = distance / speed;

        double exitCapacity = ca.getExitToCapacityMapping().get(pot);
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
                    if (es.propertyFor(otherInd).getStaticPotential().getDistance(es.propertyFor(otherInd).getCell()) >= 0) {
                        if (es.propertyFor(otherInd).getStaticPotential().getDistance(es.propertyFor(otherInd).getCell()) < distance) {
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
