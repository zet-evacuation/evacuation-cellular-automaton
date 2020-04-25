package org.zet.cellularautomaton;

import java.util.Collection;
import org.zet.cellularautomaton.potential.Potential;
import org.zetool.simulation.cellularautomaton.CellularAutomaton;

/**
 * An {@code EvacuationCellularAutomaton} represents the static structure of an evacuation scenario modelled as a
 * cellular automaton. For building evacuations the cellular automaton divides the space into multiple rooms. Each
 * {@link Room} consists of the cells of the cellular automaton. In contrast to (pseudo-)infinite areas of some cellular
 * automaton implementations, each room provides a small area. Cells can not interact with each other when they are
 * located in different rooms, even if they are neighors in the absolute coordinate system.
 *
 * For the evacuation scenario the cellular automaton profides various floor fields which assign (possibly many)
 * {@link Potential}s to the cells indicating the distance to the exit and the attractivity of cells during the
 * evacuation process. The {@code EvacuationCellularAutomaton} also contains several exits which are the safe points of
 * the evacuatio process. The exits are the positions the static floor fields poiont to.
 *
 * @author Jan-Philipp Kappmeier
 */
public interface EvacuationCellularAutomaton extends CellularAutomaton<EvacCellInterface> {

    // Speed methods
//    public double getStepsPerSecond();
//
//    public double absoluteSpeed(double relativeSpeed);
//
//    public double getSecondsPerStep();

    /**
     * 
     * @return 
     */
    public Collection<Room> getRooms();

    // new
    public Collection<Exit> getExits();
    
    public Potential getPotentialFor(Exit exit);

    /**
     * Returns a distinct {@link Potential} that can be assigned to {@link Individual}s that are safe. Typically, this
     * {@link Potential} does not point in the direction of any exit.
     * 
     * @return a potential
     */
    public Potential getSafePotential();

    public Potential minPotentialFor(EvacCellInterface c);

}
